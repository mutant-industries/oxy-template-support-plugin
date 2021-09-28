package ool.intellij.plugin.psi.reference.innerjs.globals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ool.intellij.plugin.psi.reference.innerjs.InnerJsJavaTypeConverter;
import ool.web.model.ondemand.GlobalModelProvider;

import com.google.common.collect.ImmutableList;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeParser;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/29/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableTypeProvider implements CachedValueProvider<JSType>
{
    @NonNls
    public static final String CONTROLLERS_GLOBAL_VARIABLE_NAME = "controllers";

    private static final String GLOBAL_MODEL_PROVIDER_FQN = GlobalModelProvider.class.getName();
    @NonNls
    private static final String PROVIDE_METHOD_NAME = "provide";
    @NonNls
    private static final String CONTROLLER_FQN = "org.springframework.stereotype.Controller";
    @NonNls
    private static final String CONTROLLERS_BEANS_FILE_NAME = "web.xml";

    private final PsiExpression expression;

    private final String name;

    public GlobalVariableTypeProvider(@NotNull PsiExpression expression, @Nullable String name)
    {
        this.expression = expression;
        this.name = name;
    }

    @Nullable
    @Override
    public Result<JSType> compute()
    {
        List<PsiElement> cacheDependencies = new LinkedList<>();
        cacheDependencies.add(expression);

        // controllers
        if (CONTROLLERS_GLOBAL_VARIABLE_NAME.equals(name))
        {
            Project project = expression.getProject();
            GlobalSearchScope allScope = ProjectScope.getAllScope(project);
            GlobalSearchScope projectScope = ProjectScope.getProjectScope(project);

            List<JSRecordTypeImpl.TypeMember> members = new LinkedList<>();
            JSTypeSource typeSource = JSTypeSourceFactory.createTypeSource(expression, true);
            PsiClass controller;

            PsiClass controllerAnnotation = JavaPsiFacade.getInstance(project).findClass(CONTROLLER_FQN, allScope);

            if (controllerAnnotation != null && controllerAnnotation.isAnnotationType())
            {
                for (PsiReference controllerAnnotationReference : ReferencesSearch.search(controllerAnnotation, projectScope).findAll())
                {
                    PsiElement reference = controllerAnnotationReference.getElement();

                    if ((controller = PsiTreeUtil.getParentOfType(reference, PsiClass.class)) != null
                            && controller.getQualifiedName() != null)
                    {
                        JSTypeSource source = JSTypeSourceFactory.createTypeSource(controller, true);
                        JSType jsType = JSTypeParser.createType(controller.getQualifiedName(), source);
                        JSRecordTypeImpl.PropertySignature signature = new JSRecordTypeImpl.PropertySignatureImpl(controller.getName(),
                                jsType, false, false);

                        members.add(signature);
                        cacheDependencies.add(controller);
                    }
                }
            }

            Collections.addAll(cacheDependencies, FilenameIndex.getFilesByName(project, CONTROLLERS_BEANS_FILE_NAME,
                    ProjectScope.getProjectScope(project)));

            return Result.create(new JSRecordTypeImpl(typeSource, ImmutableList.copyOf(members)), cacheDependencies);
        }

        PsiReturnStatement returnStatement;
        PsiClass aClass;
        PsiElement elementAt;

        // global model provider
        if ((returnStatement = PsiTreeUtil.getParentOfType(expression, PsiReturnStatement.class)) != null
                && (aClass = PsiTreeUtil.getParentOfType(returnStatement, PsiClass.class)) != null
                && InheritanceUtil.isInheritor(aClass, GLOBAL_MODEL_PROVIDER_FQN))
        {
            return Result.create(getTypeFromProvider(aClass), cacheDependencies);
        }
        // model provider registry
        else
        {
            elementAt = expression;

            while (elementAt != null && ! ((elementAt = elementAt.getNextSibling()) instanceof PsiExpression)) ;

            if (elementAt instanceof PsiNewExpression)
            {
                PsiJavaCodeReferenceElement providerClassReference = PsiTreeUtil.getNextSiblingOfType(elementAt.getFirstChild(),
                        PsiJavaCodeReferenceElement.class);
                PsiElement providerClass;

                if (providerClassReference != null && (providerClass = providerClassReference.resolve()) instanceof PsiClass)
                {
                    aClass = (PsiClass) providerClass;
                }
                else if ((aClass = PsiTreeUtil.getNextSiblingOfType(elementAt.getFirstChild(),
                        PsiAnonymousClass.class)) == null)
                {
                    return Result.create(null, expression);
                }

                cacheDependencies.add(aClass);

                return Result.create(getTypeFromProvider(aClass), cacheDependencies);
            }
            else if (elementAt != null)
            {
                PsiType type = ((PsiExpression) elementAt).getType();

                if (type != null && (aClass = JavaPsiFacade.getInstance(expression.getProject()).findClass(type.getCanonicalText(),
                        ProjectScope.getAllScope(expression.getProject()))) != null)
                {
                    return Result.create(getTypeFromProvider(aClass), cacheDependencies);
                }
            }
        }

        return Result.create(null, cacheDependencies);
    }

    @Nullable
    private JSType getTypeFromProvider(@NotNull PsiClass provider)
    {
        PsiMethod[] provideMethods = provider.findMethodsByName(PROVIDE_METHOD_NAME, true);
        PsiElement elementAt;
        PsiReturnStatement returnStatement;

        if (provideMethods.length < 1 || (returnStatement = PsiTreeUtil.findChildOfType(provideMethods[0], PsiReturnStatement.class)) == null
                || ! ((elementAt = returnStatement.getFirstChild()) instanceof PsiKeyword)
                || ! ((elementAt = elementAt.getNextSibling()) instanceof PsiWhiteSpace))
        {
            return null;
        }

        elementAt = elementAt.getNextSibling();

        if (elementAt instanceof PsiExpression)
        {
            return InnerJsJavaTypeConverter.getPsiElementJsType(elementAt);
        }

        return null;
    }

}
