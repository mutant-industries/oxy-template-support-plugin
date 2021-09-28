package ool.intellij.plugin.psi.reference.js;

import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 4/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DwrReferenceResolver extends JSReferenceExpressionResolver
{
    @NonNls
    private static final String DWR_REMOTE_PROXY_FQN = "org.directwebremoting.annotations.RemoteProxy";
    @NonNls
    private static final String DWR_REMOTE_METHOD_FQN = "org.directwebremoting.annotations.RemoteMethod";

    public DwrReferenceResolver(@NotNull JSReferenceExpressionImpl expression, boolean ignorePerformanceLimits)
    {
        super(expression, ignorePerformanceLimits);
    }

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull JSReferenceExpressionImpl expression, boolean incompleteCode)
    {
        PsiClass dwrClass;

        // dwr method ---------------------------------------------------------------------
        if ( ! (myRef.getParent() instanceof JSReferenceExpression)
                && myRef.getFirstChild() instanceof JSReferenceExpression)
        {
            PsiElement reference = ((JSReferenceExpression) myRef.getFirstChild()).resolve();

            if (reference instanceof PsiClass && isDwrClass(dwrClass = (PsiClass) reference))
            {
                for (PsiMethod method : dwrClass.getMethods())
                {
                    if (method.getName().equals(myReferencedName) && isDwrMethod(method))
                    {
                        return new JSResolveResult[]{new JSResolveResult(method)};
                    }
                }
            }
        }

        ResolveResult[] parentResult = super.resolve(expression, incompleteCode);

        if (parentResult.length == 0)
        {
            // dwr class ----------------------------------------------------------------------
            if (myRef.getParent() instanceof JSReferenceExpression && ! (myRef.getParent().getParent() instanceof JSReferenceExpression)
                    || ! (myRef.getParent() instanceof JSReferenceExpression) && ! (myRef.getFirstChild() instanceof JSReferenceExpression))
            {
                final GlobalSearchScope allScope = GlobalSearchScope.allScope(myRef.getProject());

                PsiClass remoteProxyAnnotation = JavaPsiFacade.getInstance(myRef.getProject())
                        .findClass(DWR_REMOTE_PROXY_FQN, allScope);

                if (remoteProxyAnnotation != null && remoteProxyAnnotation.isAnnotationType())
                {
                    for (PsiReference remoteProxyAnnotationReference : ReferencesSearch.search(remoteProxyAnnotation).findAll())
                    {
                        PsiElement reference = remoteProxyAnnotationReference.getElement();

                        if ((dwrClass = PsiTreeUtil.getParentOfType(reference, PsiClass.class)) != null
                                && dwrClass.getName().equals(myReferencedName))
                        {
                            return new JSResolveResult[]{new JSResolveResult(dwrClass)};
                        }
                    }
                }
            }
        }

        return parentResult;
    }

    public static boolean isDwrMethod(@NotNull PsiMethod method)
    {
        return OxyTemplateHelper.hasAnnotation(method, DWR_REMOTE_METHOD_FQN);
    }

    public static boolean isDwrClass(@NotNull PsiClass aClass)
    {
        return OxyTemplateHelper.hasAnnotation(aClass, DWR_REMOTE_PROXY_FQN);
    }

}
