package ool.idea.plugin.file;

import com.intellij.lang.javascript.JavaScriptSpecificHandlersFactory;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.reference.MacroReferenceResolver;
import ool.idea.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import org.jetbrains.annotations.NotNull;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewSpecificHandlersFactory extends JavaScriptSpecificHandlersFactory
{
    @NotNull
    @Override
    public JSResolveUtil.Resolver<JSReferenceExpressionImpl> createReferenceExpressionResolver(JSReferenceExpressionImpl referenceExpression,
                                                                                               PsiFile containingFile)
    {
        return new InnerJsReferenceExpressionResolver(referenceExpression, containingFile)
        {
            @NotNull
            @Override
            protected MacroReferenceResolver getMacroReferenceResolver()
            {
                return new MacroReferenceResolver()
                {
                    @Override
                    protected void addJsMacroReferences(@NotNull List<PsiElement> result, @NotNull String macroName, @NotNull PsiElement referencingElement,  @NotNull Project project)
                    {
                        result.addAll(OxyTemplateIndexUtil.getJsMacroNameReferencesForCompiledTemplate(macroName, referencingElement.getContainingFile()));
                    }
                };
            }
        };
    }

}
