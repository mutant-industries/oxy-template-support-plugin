package ool.intellij.plugin.file;

import ool.intellij.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import ool.intellij.plugin.psi.reference.innerjs.InnerJsTypeEvaluator;

import com.intellij.lang.javascript.nashorn.NashornJSSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.resolve.JSTypeProcessor;
import com.intellij.psi.PsiFile;
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
    public JSTypeEvaluator newTypeEvaluator(JSEvaluateContext context, JSTypeProcessor processor, boolean ecma)
    {
        return new InnerJsTypeEvaluator(context, processor);
    }

}
