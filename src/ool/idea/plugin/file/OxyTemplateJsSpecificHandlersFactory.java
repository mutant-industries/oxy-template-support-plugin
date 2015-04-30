package ool.idea.plugin.file;

import com.intellij.lang.javascript.completion.JSCompletionKeywordsContributor;
import com.intellij.lang.javascript.nashorn.NashornJSSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.editor.completion.InnerJsNewKeywordsContributor;
import ool.idea.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import ool.idea.plugin.psi.reference.innerjs.InnerJsTypeEvaluator;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateJsSpecificHandlersFactory extends NashornJSSpecificHandlersFactory
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

    @NotNull
    @Override
    public JSTypeEvaluator newTypeEvaluator(BaseJSSymbolProcessor.EvaluateContext context, BaseJSSymbolProcessor.TypeProcessor processor, boolean ecma)
    {
        return new InnerJsTypeEvaluator(context, processor);
    }

}
