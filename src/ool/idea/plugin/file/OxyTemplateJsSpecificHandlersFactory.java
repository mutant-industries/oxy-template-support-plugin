package ool.idea.plugin.file;

import com.intellij.lang.javascript.JavaScriptSpecificHandlersFactory;
import com.intellij.lang.javascript.completion.JSCompletionKeywordsContributor;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.editor.completion.InnerJsNewKeywordsContributor;
import ool.idea.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateJsSpecificHandlersFactory extends JavaScriptSpecificHandlersFactory
{
    @NotNull
    @Override
    public JSResolveUtil.Resolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression,
                                                                                               PsiFile containingFile)
    {
        return new InnerJsReferenceExpressionResolver(referenceExpression, containingFile);
    }

    @NotNull
    @Override
    public JSCompletionKeywordsContributor newCompletionKeywordsContributor()
    {
        return new InnerJsNewKeywordsContributor();
    }

}
