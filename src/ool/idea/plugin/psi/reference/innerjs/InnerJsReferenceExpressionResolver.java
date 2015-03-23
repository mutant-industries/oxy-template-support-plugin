package ool.idea.plugin.psi.reference.innerjs;

import com.intellij.lang.javascript.nashorn.resolve.NashornJSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.idea.plugin.psi.reference.MacroReferenceResolver;
import ool.idea.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsReferenceExpressionResolver extends NashornJSReferenceExpressionResolver
{
    public InnerJsReferenceExpressionResolver(JSReferenceExpressionImpl expression, PsiFile file)
    {
        super(expression, file);
    }

    @Override
    public ResolveResult[] doResolve()
    {
        if (myReferencedName == null)
        {
            return ResolveResult.EMPTY_ARRAY;
        }

        if(PsiTreeUtil.getParentOfType(myRef, JSCallExpression.class) != null)
        {
            ResolveResult[] results = ResolveCache.getInstance(myRef.getProject())
                    .resolveWithCaching(myRef, getMacroReferenceResolver(), false, false, myRef.getContainingFile());

            if(results.length > 1 && myRef.getParent() instanceof JSCallExpression)
            {
                ResolveResult result;

                if((result = OxyTemplateHelper.multiResolveWithIncludeSearch(myRef, results)) != null)
                {
                    return new ResolveResult[]{result};
                }

                return results;
            }
            else if(results.length > 0)
            {
                return results;
            }
        }

        ResolveResult[] parentResult = super.doResolve();

        if(parentResult == null || parentResult.length == 0)
        {
            PsiElement reference;
            // global
            if((reference = OxyTemplateIndexUtil.getGlobalVariableRefrence(myReferencedName, myContainingFile.getProject())) != null
                    && reference instanceof PsiLiteralExpression)
            {
                return new JSResolveResult[]{new JSResolveResult(new GlobalVariableDefinition((PsiLiteralExpression)reference))};
            }
        }

        return parentResult;
    }

    @NotNull
    protected MacroReferenceResolver getMacroReferenceResolver()
    {
        return MacroReferenceResolver.DEFAULT;
    }

}
