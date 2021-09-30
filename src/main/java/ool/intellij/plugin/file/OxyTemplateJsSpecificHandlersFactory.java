package ool.intellij.plugin.file;

import ool.intellij.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import ool.intellij.plugin.psi.reference.innerjs.InnerJsTypeEvaluator;

import com.intellij.lang.javascript.nashorn.NashornJSSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.psi.impl.source.resolve.ResolveCache.PolyVariantResolver;
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
    public PolyVariantResolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression,
                                                                                                         boolean ignorePerformanceLimits)
    {
        return new InnerJsReferenceExpressionResolver(referenceExpression, ignorePerformanceLimits);
    }

    @NotNull
    @Override
    public JSTypeEvaluator newTypeEvaluator(@NotNull JSEvaluateContext context)
    {
        return new InnerJsTypeEvaluator(context);
    }

}
