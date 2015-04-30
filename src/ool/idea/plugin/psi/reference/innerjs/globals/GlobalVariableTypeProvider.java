package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.LinkedList;
import java.util.List;
import ool.web.model.ondemand.GlobalModelProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/29/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableTypeProvider implements CachedValueProvider<PsiType>
{
    private static final String GLOBAL_MODEL_PROVIDER_FQN = GlobalModelProvider.class.getName();
    @NonNls
    public static final String PROVIDE_METHOD_NAME = "provide";

    private final PsiLiteralExpression literalExpression;

    public GlobalVariableTypeProvider(@NotNull PsiLiteralExpression literalExpression)
    {
        this.literalExpression = literalExpression;
    }

    @Nullable
    @Override
    public Result<PsiType> compute()
    {
        if ( ! (literalExpression.getValue() instanceof String) || literalExpression.getValue()
                .equals(GlobalVariableDefinition.CONTROLLERS_GLOBAL_VARIABLE_NAME))
        {
            return Result.create(null, literalExpression);
        }

        PsiReturnStatement returnStatement;
        PsiClass aClass;
        PsiElement elementAt;
        List<PsiElement> cacheDependencies = new LinkedList<>();
        cacheDependencies.add(literalExpression);

        // global model provider
        if ((returnStatement = PsiTreeUtil.getParentOfType(literalExpression, PsiReturnStatement.class)) != null
                && (aClass = PsiTreeUtil.getParentOfType(returnStatement, PsiClass.class)) != null
                && InheritanceUtil.isInheritor(aClass, GLOBAL_MODEL_PROVIDER_FQN))
        {
            return Result.create(getTypeFromProvider(aClass), cacheDependencies);
        }
        // register via model provider registry
        else
        {
            elementAt = literalExpression;

            while (elementAt != null && ! ((elementAt = elementAt.getNextSibling()) instanceof PsiExpression));

            if (elementAt instanceof PsiNewExpression)
            {
                /** TODO duplicated code to {@link ool.idea.plugin.psi.reference.innerjs.ExtenderProvider#getExtenderFromProvider} */
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
                    return Result.create(null, literalExpression);
                }
                // ------------------------------------------------------------------------------------------------------

                cacheDependencies.add(aClass);

                return Result.create(getTypeFromProvider(aClass), cacheDependencies);
            }
            else if (elementAt != null)
            {
                PsiType type = ((PsiExpression) elementAt).getType();

                if (type != null && (aClass = JavaPsiFacade.getInstance(literalExpression.getProject()).findClass(type.getCanonicalText(),
                        ProjectScope.getAllScope(literalExpression.getProject()))) != null)
                {
                    return Result.create(getTypeFromProvider(aClass), cacheDependencies);
                }
            }
        }

        return Result.create(null, cacheDependencies);
    }

    @Nullable
    private static PsiType getTypeFromProvider(@NotNull PsiClass aClass)
    {
        PsiMethod[] provideMethods = aClass.findMethodsByName(PROVIDE_METHOD_NAME, true);
        PsiElement elementAt;
        PsiReturnStatement returnStatement;

        if (provideMethods.length < 1 || (returnStatement = PsiTreeUtil.findChildOfType(provideMethods[0], PsiReturnStatement.class)) == null
                || ! ((elementAt = returnStatement.getFirstChild()) instanceof PsiKeyword)
                || ! ((elementAt = elementAt.getNextSibling()) instanceof PsiWhiteSpace))
        {
            return null;
        }

        elementAt = elementAt.getNextSibling();
        PsiExpression referenceExpression;

        if (elementAt instanceof PsiExpression)
        {
            referenceExpression = (PsiExpression) elementAt;

            if (referenceExpression.getType() != null)
            {
                return referenceExpression.getType();
            }
        }

        return null;
    }

}
