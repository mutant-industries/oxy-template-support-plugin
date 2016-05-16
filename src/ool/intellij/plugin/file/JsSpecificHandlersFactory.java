package ool.intellij.plugin.file;

import ool.intellij.plugin.psi.reference.js.DwrReferenceResolver;

import com.intellij.lang.javascript.JavaScriptSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.psi.PsiFile;
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
    public JSResolveUtil.Resolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression,
                                                                                               PsiFile containingFile)
    {
        return new DwrReferenceResolver(referenceExpression, containingFile);
    }

}