package ool.intellij.plugin.psi.reference.innerjs;

import com.intellij.codeInsight.daemon.impl.analysis.JavaGenericsUtil;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSTypeEvaluatorHelper;
import com.intellij.lang.javascript.psi.JSCommonTypeNames;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.JSTypeUtils;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsJavaTypeConverter extends NashornJSTypeEvaluatorHelper
{
    // TODO temp code, see https://youtrack.jetbrains.com/issue/WEB-16383
    @Override
    public boolean addTypeFromResolveResult(JSTypeEvaluator evaluator, PsiElement result, boolean hasSomeType)
    {
        JSType type;

        if (result instanceof PsiMember && (type = getPsiElementJsType(result)) != null)
        {
            evaluator.addType(type, result);

            return true;
        }

        return super.addTypeFromResolveResult(evaluator, result, hasSomeType);
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
