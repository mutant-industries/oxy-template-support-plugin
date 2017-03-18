package ool.intellij.plugin.file;

import ool.intellij.plugin.psi.reference.js.DwrReferenceResolver;

import com.intellij.lang.javascript.JavaScriptSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache.PolyVariantResolver;
import org.jetbrains.annotations.NotNull;

/**
 * 4/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsSpecificHandlersFactory extends JavaScriptSpecificHandlersFactory
{
    @NotNull
    @Override
    public PolyVariantResolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression,
                                                                                                         boolean ignorePerformanceLimits)
    {
        return new DwrReferenceResolver(referenceExpression, ignorePerformanceLimits);
    }

}
