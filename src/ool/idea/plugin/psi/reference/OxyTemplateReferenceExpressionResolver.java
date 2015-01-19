package ool.idea.plugin.psi.reference;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateReferenceExpressionResolver extends JSReferenceExpressionResolver
{
    public OxyTemplateReferenceExpressionResolver(JSReferenceExpressionImpl expression, PsiFile file)
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

        PsiElement reference;

        String text = myRef.getElement().getText();

        if(PsiTreeUtil.getParentOfType(myRef, JSCallExpression.class) != null)
        {
            if((reference = OxyTemplateIndexUtil.getMacroNameReference(myRef.getText(), myContainingFile.getProject())) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        ResolveResult[] parentResult = super.doResolve();

        if((parentResult == null || parentResult.length == 0) && text.equals(myReferencedName))
        {
            // global
            if((reference = OxyTemplateIndexUtil.getGlobalVariableRefrence(myReferencedName, myContainingFile.getProject())) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        return parentResult;
    }

}
