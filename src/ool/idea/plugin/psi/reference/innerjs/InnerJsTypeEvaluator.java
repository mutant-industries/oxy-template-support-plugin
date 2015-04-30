package ool.idea.plugin.psi.reference.innerjs;

import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.daemon.impl.analysis.JavaGenericsUtil;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSTypeEvaluator;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSForInStatement;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.JSTypeUtils;
import com.intellij.lang.javascript.psi.JSVarStatement;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.types.JSArrayTypeImpl;
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.search.AllClassesSearchExecutor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.editor.completion.InnerJsNewKeywordsContributor;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroParam;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.idea.plugin.psi.macro.param.MacroParamHelper;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.idea.plugin.psi.macro.param.descriptor.MacroParamDescriptor;
import ool.idea.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO cleanup of {@link InnerJsTypeEvaluator#getPsiMemberJsType(PsiMember)} and related stuff
 * - see https://youtrack.jetbrains.com/issue/WEB-16383
 * <p/>
 * 4/23/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsTypeEvaluator extends NashornJSTypeEvaluator
{
    @NonNls
    private static final String CONTROLLER_FQN = "org.springframework.stereotype.Controller";
    @NonNls
    private static final String CONTROLLERS_BEANS_FILE_NAME = "web.xml";
    @NonNls
    private static final String HIBERNATE_ENTITY_ANNOTETION_GQN = "javax.persistence.Entity";
    @NonNls
    public static final String REPEAT_MACRO_VARIABLE_DEFINITION = "varName";
    @NonNls
    public static final String REPEAT_MACRO_LIST_DEFINITION = "list";

    private static final Key<CachedValue<JSType>> CONTROLLERS_TYPE_CACHE_KEY = Key.create("CONTROLLERS_TYPE_CACHE_KEY");

    public InnerJsTypeEvaluator(BaseJSSymbolProcessor.EvaluateContext context, BaseJSSymbolProcessor.TypeProcessor processor)
    {
        super(context, processor);
    }

    @Override
    public void addType(@Nullable final JSType type, @Nullable PsiElement source)
    {
        JSProperty macro;

        if (myContext.getSource() instanceof JSParameter
                && (macro = checkMacroFirstParameter((JSParameter) myContext.getSource())) != null)
        {
            super.addType(getMacroFirstParameterType(macro, type), source);
        }
        else
        {
            super.addType(type, source);
        }
    }

    @Override
    protected void addTypeFromVariableResolveResult(@NotNull JSReferenceExpression expression, @NotNull JSVariable jsVariable)
    {
        JSProperty macro;
        String modifiedType;

        // macro first parameter
        if (jsVariable instanceof JSParameter && (macro = checkMacroFirstParameter((JSParameter) jsVariable)) != null)
        {
            super.addType(getMacroFirstParameterType(macro, null), null);

            return;
        }
        // function parameter - TODO fix collextion type in foreach
        else if (jsVariable instanceof JSParameter && jsVariable.getType() != null && StringUtils.isNotEmpty((modifiedType = StringUtils
                .join(parseJavaSimplifiedRawType(jsVariable.getType().getTypeText(), myContext.getSource().getProject()), "|"))))
        {
            super.addType(JSTypeUtils.createType(modifiedType, jsVariable.getType().getSource()), null);

            return;
        }

        super.addTypeFromVariableResolveResult(expression, jsVariable);
    }

    @Override
    protected boolean addTypeFromResolveResult(JSReferenceExpression expression, PsiElement parent, @NotNull PsiElement resolveResult, boolean hasSomeType)
    {
        JSType type;

        // for each / oxy.repeat
        if ((type = checkForEachDefinition(resolveResult)) != null)
        {
            super.addType(type, resolveResult);
        }
        // globals
        else if (resolveResult instanceof GlobalVariableDefinition)
        {
            GlobalVariableDefinition variableDefinition = (GlobalVariableDefinition) resolveResult;
            PsiType variableType;

            if (GlobalVariableDefinition.CONTROLLERS_GLOBAL_VARIABLE_NAME.equals(variableDefinition.getName()))
            {
                super.addType(getControllersType(variableDefinition, myContext.targetFile, myContext.targetFile.getProject()), null);

                return true;
            }
            else if ((variableType = variableDefinition.getType()) != null)
            {

                JSTypeSource typeSource = new JSTypeSource(myContext.targetFile, variableDefinition.getOriginalElement(),
                        JSTypeSource.SourceLanguage.JS, true);

                super.addType(JSTypeUtils.createType(simplifyModifiedType(modifyCollectionType(variableType, variableDefinition.getProject())),
                        typeSource), null);

                return true;
            }
        }
        // to be removed ----------------------------------------------------------------------------------------------
        else if (resolveResult instanceof PsiMember && (type = getPsiMemberJsType((PsiMember) resolveResult)) != null)
        {
            super.addType(type, resolveResult);
        }
        // -------------------------------------------------------------------------------------------------------------

        return super.addTypeFromResolveResult(expression, parent, resolveResult, hasSomeType);
    }

    @NotNull
    private JSType getMacroFirstParameterType(@NotNull JSProperty macro, @Nullable JSType originalType)
    {
        assert OxyTemplateIndexUtil.isMacro(macro);

        MacroParamSuggestionSet jsMacroParamSuggestions = MacroParamHelper
                .getJsMacroParamSuggestions(macro, false);
        JSTypeSource typeSource = new JSTypeSource(myContext.targetFile, myContext.getSource(), JSTypeSource.SourceLanguage.JS, true);
        List<JSRecordTypeImpl.TypeMember> members = new LinkedList<>();

        // @param {type} params.name
        if (originalType instanceof JSRecordTypeImpl)
        {
            MacroParamDescriptor descriptor;

            for (JSRecordTypeImpl.TypeMember typeMember : ((JSRecordTypeImpl) originalType).getTypeMembers())
            {
                if ( ! (typeMember instanceof JSRecordTypeImpl.PropertySignature)
                        || ! (((JSRecordTypeImpl.PropertySignature) typeMember).getType() instanceof JSTypeImpl)
                        || (descriptor = jsMacroParamSuggestions.getByName(((JSRecordTypeImpl.PropertySignature) typeMember).name)) == null
                        || ! descriptor.isDocumented())
                {
                    members.add(typeMember);
                    continue;
                }

                JSType jsType = JSTypeUtils.createType(descriptor.getType(), typeSource);
                JSRecordTypeImpl.PropertySignature signature = new JSRecordTypeImpl.PropertySignature(descriptor.getName(),
                        jsType, ! descriptor.isRequired());

                members.add(signature);
            }
        }
        // @param {type} name
        else if (originalType == null)
        {
            for (MacroParamDescriptor paramDescriptor : jsMacroParamSuggestions)
            {
                if (! paramDescriptor.isDocumented())
                {
                    continue;
                }

                JSType jsType = JSTypeUtils.createType(paramDescriptor.getType(), typeSource);

                JSRecordTypeImpl.PropertySignature signature = new JSRecordTypeImpl.PropertySignature(paramDescriptor.getName(),
                        jsType, ! paramDescriptor.isRequired());

                members.add(signature);
            }
        }

        return new JSRecordTypeImpl(typeSource, ImmutableList.copyOf(members));
    }

    @NotNull
    private JSType getControllersType(@NotNull final GlobalVariableDefinition controllersVar, @NotNull final PsiFile scope,
                                      @NotNull final Project project)
    {
        CachedValue<JSType> cached = project.getUserData(CONTROLLERS_TYPE_CACHE_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<JSType>()
            {
                @Nullable
                @Override
                public Result<JSType> compute()
                {
                    final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);
                    List<JSRecordTypeImpl.TypeMember> members = new LinkedList<>();
                    JSTypeSource typeSource = new JSTypeSource(scope, controllersVar.getNavigationElement(), JSTypeSource.SourceLanguage.JS, true);
                    List<PsiElement> cacheDependencies = new LinkedList<>();
                    PsiClass controller;

                    PsiClass controllerAnnotation = JavaPsiFacade.getInstance(project)
                            .findClass(CONTROLLER_FQN, allScope);

                    if (controllerAnnotation != null && controllerAnnotation.isAnnotationType())
                    {
                        for (PsiReference controllerAnnotationRefernce : ReferencesSearch.search(controllerAnnotation).findAll())
                        {
                            PsiElement reference = controllerAnnotationRefernce.getElement();

                            if ((controller = PsiTreeUtil.getParentOfType(reference, PsiClass.class)) != null
                                    && controller.getQualifiedName() != null)
                            {
                                JSTypeSource source = new JSTypeSource(scope, controller, JSTypeSource.SourceLanguage.JS, true);
                                JSType jsType = JSTypeUtils.createType(controller.getQualifiedName(), source);
                                JSRecordTypeImpl.PropertySignature signature = new JSRecordTypeImpl.PropertySignature(controller.getName(),
                                        jsType, false);

                                members.add(signature);
                                cacheDependencies.add(controller);
                            }
                        }
                    }

                    Collections.addAll(cacheDependencies, FilenameIndex.getFilesByName(project, CONTROLLERS_BEANS_FILE_NAME,
                            ProjectScope.getProjectScope(project)));

                    cacheDependencies.add(controllersVar.getNavigationElement());

                    return Result.create((JSType) new JSRecordTypeImpl(typeSource, ImmutableList.copyOf(members)), cacheDependencies);
                }
            }, false);

            project.putUserData(CONTROLLERS_TYPE_CACHE_KEY, cached);
        }

        assert cached.getValue() != null;

        return cached.getValue();
    }

    @Nullable
    private static JSType checkForEachDefinition(@NotNull final PsiElement element)
    {
        PsiElement elementLocal = element;

        if (elementLocal.getParent() instanceof JSVarStatement)
        {
            elementLocal = elementLocal.getParent();
        }

        // for each
        if ((elementLocal instanceof JSDefinitionExpression || elementLocal instanceof JSVarStatement)
                && elementLocal.getParent() instanceof JSForInStatement)
        {
            JSForInStatement forInStatement = (JSForInStatement) elementLocal.getParent();

            if ( ! forInStatement.getFirstChild().getText().endsWith(InnerJsNewKeywordsContributor.EACH))
            {
                return null;
            }

            PsiElement collectionExpression = forInStatement.getCollectionExpression();

            if (collectionExpression instanceof JSReferenceExpression)
            {
                collectionExpression = ((JSReferenceExpression) collectionExpression).resolve();
            }

            JSType collectionType;

            if ( ! (collectionExpression instanceof PsiMember) ||
                    (collectionType = getPsiMemberJsType((PsiMember) collectionExpression)) == null)
            {
                collectionType = JSTypeUtils.getTypeOfElement(collectionExpression);
            }

            if ( ! (collectionType instanceof JSArrayTypeImpl))
            {
                return null;
            }

            return ((JSArrayTypeImpl) collectionType).getType();
        }
        // oxy.repeat
        else if ( ! (elementLocal instanceof PsiPackage) && elementLocal.getFirstChild() instanceof JSVariable)
        {
            PsiElement elementAt = elementLocal.getContainingFile().getViewProvider()
                    .findElementAt(elementLocal.getNode().getStartOffset(), OxyTemplate.INSTANCE);

            assert elementAt != null;

            MacroAttribute attribute = PsiTreeUtil.getParentOfType(elementAt, MacroAttribute.class);

            if (attribute == null || ! REPEAT_MACRO_VARIABLE_DEFINITION.equals(attribute.getMacroParamName().getText()))
            {
                return null;
            }

            MacroCall macroCall = PsiTreeUtil.getParentOfType(attribute, MacroCall.class);

            assert macroCall != null;

            for (MacroAttribute macroAttribute : macroCall.getMacroAttributeList())
            {
                if (REPEAT_MACRO_LIST_DEFINITION.equals(macroAttribute.getMacroParamName().getText()))
                {
                    MacroParam macroParam;

                    if ((macroParam = macroAttribute.getMacroParam()) == null)
                    {
                        return null;
                    }

                    PsiElement list = elementLocal.getContainingFile().getViewProvider()
                            .findElementAt(macroParam.getNode().getStartOffset(), OxyTemplateInnerJs.INSTANCE);

                    assert list != null;

                    PsiElement reference;
                    JSExpressionStatement statement = PsiTreeUtil.getNextSiblingOfType(list, JSExpressionStatement.class);

                    if (statement != null && statement.getFirstChild() instanceof JSReferenceExpression
                            && (reference = ((JSReferenceExpression) statement.getFirstChild()).resolve()) != null)
                    {
                        JSType collectionType;

                        if ( ! (reference instanceof PsiMember) ||
                                (collectionType = getPsiMemberJsType((PsiMember) reference)) == null)
                        {
                            collectionType = JSTypeUtils.getTypeOfElement(reference);
                        }

                        if ( ! (collectionType instanceof JSArrayTypeImpl))
                        {
                            return null;
                        }

                        return ((JSArrayTypeImpl) collectionType).getType();
                    }
                }
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * TODO refactoring
     *
     * @param parameter
     * @return if parameter is the first parameter of a macro, return macro, null otherwise
     */
    @Nullable
    private static JSProperty checkMacroFirstParameter(@Nullable JSParameter parameter)
    {
        JSProperty macro;
        JSFunctionExpression functionExpression;

        if ((macro = PsiTreeUtil.getParentOfType(parameter, JSProperty.class)) != null
                && OxyTemplateIndexUtil.isMacro(macro) && macro.getLastChild() instanceof JSFunctionExpression)
        {
            functionExpression = (JSFunctionExpression) macro.getLastChild();

            if (functionExpression.getParameters().length > 0 && functionExpression.getParameters()[0].isEquivalentTo(parameter))
            {
                return macro;
            }
        }

        return null;
    }

    /**
     * Bean|String[] -> {"package.Bean", "java.lang.String[]"}
     *
     * @param rawType
     * @param project
     * @return
     */
    @NotNull
    public static List<String> parseJavaSimplifiedRawType(@Nullable String rawType, @NotNull Project project)
    {
        List<String> result = new LinkedList<>();

        if (rawType == null)
        {
            return result;
        }

        GlobalSearchScope scope = ProjectScope.getProjectScope(project);

        /** TODO ? use {@link JSTypeUtils#createType} for parsing */
        main:
        for (String simpleTypeName : rawType.split("\\|"))
        {
            simpleTypeName = simpleTypeName.trim();
            boolean isCollection = simpleTypeName.endsWith("[]");
            simpleTypeName = simpleTypeName.replaceFirst("\\s*(\\[\\])?$", "");

            if (StringUtils.isEmpty(simpleTypeName))
            {
                continue;
            }

            if (Character.isUpperCase(simpleTypeName.charAt(0)) && ! simpleTypeName.contains("."))
            {
                final List<PsiClass> classes = new LinkedList<>();

                AllClassesSearchExecutor.processClassesByNames(project, scope, Collections.singletonList(simpleTypeName),
                    new Processor<PsiClass>()
                    {
                        @Override
                        public boolean process(PsiClass psiClass)
                        {
                            classes.add(psiClass);

                            return true;
                        }
                    });

                if (classes.size() == 1)
                {
                    result.add(classes.get(0).getQualifiedName() + (isCollection ? "[]" : ""));
                    continue;
                }
                else if (classes.size() > 1)
                {
                    for (PsiClass aClass : classes)
                    {
                        if (OxyTemplateHelper.hasAnnotation(aClass, HIBERNATE_ENTITY_ANNOTETION_GQN) || ExtenderProvider.getExtenders(aClass).size() != 0)
                        {
                            result.add(aClass.getQualifiedName() + (isCollection ? "[]" : ""));
                            continue main;
                        }
                    }
                }

                result.add(simpleTypeName + (isCollection ? "[]" : ""));
            }
            else
            {
                result.add(simpleTypeName + (isCollection ? "[]" : ""));
            }
        }

        return result;
    }

    private static String modifyCollectionType(@NotNull final PsiType originalType, @NotNull Project project)
    {
        GlobalSearchScope scope = ProjectScope.getProjectScope(project);
        PsiType collectionType = JavaGenericsUtil.getCollectionItemType(originalType, scope);

        if (collectionType != null)
        {
            return collectionType.getCanonicalText() + "[]";
        }

        return originalType.getCanonicalText();
    }

    // ------- tenp code, should be fixed in https://youtrack.jetbrains.com/issue/WEB-16383 ----------------------------
    @Nullable
    public static JSType getPsiMemberJsType(@Nullable PsiMember psiMember)
    {
        if (psiMember instanceof PsiField)
        {
            PsiField psiField = (PsiField) psiMember;

            JSTypeSource typeSource = new JSTypeSource(psiField.getContainingFile(), psiField, JSTypeSource.SourceLanguage.JS, true);
            String modifiedType = modifyCollectionType(psiField.getType(), psiField.getProject());

            return JSTypeUtils.createType(simplifyModifiedType(modifiedType), typeSource);
        }
        else if (psiMember instanceof PsiMethod)
        {
            PsiMethod psiMethod = (PsiMethod) psiMember;

            if (psiMethod.getReturnType() != null)
            {
                JSTypeSource typeSource = new JSTypeSource(psiMethod.getContainingFile(), psiMethod, JSTypeSource.SourceLanguage.JS, true);

                String modifiedType = modifyCollectionType(psiMethod.getReturnType(), psiMethod.getProject());

                return JSTypeUtils.createType(simplifyModifiedType(modifiedType), typeSource);
            }
        }

        return null;
    }

    @Nullable
    public static String simplifyModifiedType(@Nullable String type)
    {
        if(type == null)
        {
            return null;
        }
        else if (type.equals(String.class.getName()))
        {
            return "string";
        }
        else if (type.equals(Integer.class.getName())
                || type.equals(Long.class.getName())
                || type.equals(Number.class.getName())
                || type.equals(Short.class.getName())
                || type.equals(BigDecimal.class.getName()))
        {
            return "number";
        }
        else if (type.startsWith(Map.class.getName()))
        {
            return "object[]";
        }
        else if (type.equals(Object.class.getName()))
        {
            return "object";
        }
        else if (type.equals(Boolean.class.getName()))
        {
            return "boolean";
        }

        return type;
    }
    // -----------------------------------------------------------------------------------------------------------------

}
