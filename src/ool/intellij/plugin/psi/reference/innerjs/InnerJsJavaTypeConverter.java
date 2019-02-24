package ool.intellij.plugin.psi.reference.innerjs;

import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;
import ool.intellij.plugin.psi.MacroAttribute;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.MacroParam;

import com.intellij.codeInsight.daemon.impl.analysis.JavaGenericsUtil;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSTypeEvaluatorHelper;
import com.intellij.lang.javascript.psi.JSCommonTypeNames;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.JSTypeUtils;
import com.intellij.lang.javascript.psi.JSVarStatement;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSSimpleTypeProcessor;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.types.JSArrayTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsJavaTypeConverter extends NashornJSTypeEvaluatorHelper
{
    @Override
    public boolean addTypeFromResolveResult(@NotNull JSTypeEvaluator evaluator, @NotNull JSEvaluateContext context,
                                            @NotNull PsiElement result)
    {
        JSType type;

        if ((type = checkForEachDefinition(result)) != null)
        {
            evaluator.addType(type, result);

            return true;
        }
        // TODO temp code, see https://youtrack.jetbrains.com/issue/WEB-16383
        if (result instanceof PsiMember && (type = getPsiElementJsType(result)) != null)
        {
            evaluator.addType(type, result);

            return true;
        }

        return super.addTypeFromResolveResult(evaluator, context, result);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    private static JSType checkForEachDefinition(@NotNull final PsiElement element)
    {
        PsiElement elementLocal = element;

        if (elementLocal.getParent() instanceof JSVarStatement)
        {
            elementLocal = elementLocal.getParent();
        }

        // repeat macro - var keyword is missing
        if (elementLocal instanceof PsiPackage || ! (elementLocal.getFirstChild() instanceof JSVariable))
        {
            return null;
        }

        PsiElement elementAt = elementLocal.getContainingFile().getViewProvider()
                .findElementAt(elementLocal.getNode().getStartOffset(), OxyTemplate.INSTANCE);

        assert elementAt != null;

        MacroAttribute attribute = PsiTreeUtil.getParentOfType(elementAt, MacroAttribute.class);

        if (attribute == null || ! MacroIndex.REPEAT_MACRO_VARIABLE_DEFINITION.equals(attribute.getMacroParamName().getText()))
        {
            return null;
        }

        MacroCall macroCall = PsiTreeUtil.getParentOfType(attribute, MacroCall.class);

        assert macroCall != null;

        for (MacroAttribute macroAttribute : macroCall.getMacroAttributeList())
        {
            if (MacroIndex.REPEAT_MACRO_LIST_DEFINITION.equals(macroAttribute.getMacroParamName().getText()))
            {
                MacroParam macroParam;

                if ((macroParam = macroAttribute.getMacroParam()) == null)
                {
                    return null;
                }

                PsiElement list = elementLocal.getContainingFile().getViewProvider().findElementAt(macroParam.getNode().getStartOffset()
                        + macroParam.getTextLength() - 1, OxyTemplateInnerJs.INSTANCE);

                JSReferenceExpression statement = PsiTreeUtil.getParentOfType(list, JSReferenceExpression.class);

                if (statement != null)
                {
                    JSSimpleTypeProcessor typeProcessor = new JSSimpleTypeProcessor();

                    JSTypeEvaluator.evaluateTypes(statement, statement.getContainingFile(), typeProcessor);

                    if (typeProcessor.getType() instanceof JSArrayTypeImpl)
                    {
                        return ((JSArrayTypeImpl) typeProcessor.getType()).getType();
                    }
                }
            }
        }

        return null;
    }
    // -------------------------------------------------------------------

    @Nullable
    public static JSType getPsiElementJsType(@Nullable PsiElement element)
    {
        PsiType type = null;

        if (element instanceof PsiField)
        {
            type = ((PsiField) element).getType();
        }
        else if (element instanceof PsiMethod)
        {
            type = ((PsiMethod) element).getReturnType();
        }
        else if (element instanceof PsiExpression)
        {
            type = ((PsiExpression) element).getType();
        }

        if (type != null)
        {
            JSTypeSource typeSource = JSTypeSourceFactory.createTypeSource(element, true);

            return JSTypeUtils.createType(modifyCollectionType(type, element.getProject()), typeSource);
        }

        return null;
    }

    @NotNull
    public static String modifyCollectionType(@NotNull final PsiType originalType, @NotNull Project project)
    {
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        PsiType collectionType = JavaGenericsUtil.getCollectionItemType(originalType, scope);

        if (collectionType != null)
        {
            return simplify(collectionType.getCanonicalText()) + "[]";
        }

        return simplify(originalType.getCanonicalText());
    }

    @NotNull
    public static String simplify(@NotNull String javaFqn)
    {
        if (javaFqn.equals(String.class.getName()))
        {
            return JSCommonTypeNames.STRING_TYPE_NAME;
        }
        else if (javaFqn.equals(Integer.class.getName())
                || javaFqn.equals(Long.class.getName())
                || javaFqn.equals(Number.class.getName())
                || javaFqn.equals(Short.class.getName()))
        {
            return JSCommonTypeNames.NUMBER_TYPE_NAME;
        }
        else if (javaFqn.equals(Object.class.getName()))
        {
            return JSCommonTypeNames.ANY_TYPE_NAME;
        }
        else if (javaFqn.equals(Boolean.class.getName()))
        {
            return JSCommonTypeNames.BOOLEAN_TYPE_NAME;
        }

        return javaFqn;
    }

}
