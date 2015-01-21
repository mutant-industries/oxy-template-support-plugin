package ool.idea.plugin.psi.reference;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsReferenceExpressionResolver extends JSReferenceExpressionResolver
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

        String text = myRef.getElement().getText();

        if(PsiTreeUtil.getParentOfType(myRef, JSCallExpression.class) != null)
        {
            List<PsiElement> references = OxyTemplateIndexUtil.getMacroNameReferences(myRef.getText(),
                    myContainingFile.getProject());

            if(references.size() > 0)
            {
                JSResolveResult[] result = new JSResolveResult[references.size()];

                for(int i = 0; i < references.size(); i++)
                {
                    result[i] = new JSResolveResult(references.get(i));
                }

                return result;
            }
        }

        ResolveResult[] parentResult = super.doResolve();

        if((parentResult == null || parentResult.length == 0) && text.equals(myReferencedName))
        {
            PsiElement reference;
            // global
            if((reference = OxyTemplateIndexUtil.getGlobalVariableRefrence(myReferencedName, myContainingFile.getProject())) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        return parentResult;
    }

}
