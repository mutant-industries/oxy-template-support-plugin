package ool.intellij.plugin.editor.completion.handler;

import ool.intellij.plugin.file.RelativePathCalculator;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.OxyTemplateElementFactory;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.web.template.impl.chunk.directive.IncludeOnceDirective;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.InsertHandlerDecorator;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.lang.javascript.psi.JSElement;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludeAutoInsert implements InsertHandlerDecorator<LookupElement>
{
    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElementDecorator<LookupElement> item)
    {
        item.getDelegate().handleInsert(context);

        if ( ! (item.getObject() instanceof JSElement))
        {
            return;
        }

        JSElement macroDefinition = (JSElement) item.getObject();

        if (OxyTemplateHelper.isJsMacroMissingInclude(context.getFile().getViewProvider().getPsi(OxyTemplate.INSTANCE), macroDefinition))
        {
            RelativePathCalculator pathCalculator = new RelativePathCalculator(context.getFile().getVirtualFile().getPath(),
                    macroDefinition.getContainingFile().getVirtualFile().getPath());
            pathCalculator.execute();

            DirectiveStatement includeDirective = OxyTemplateElementFactory
                    .createDirectiveStatement(context.getProject(), IncludeOnceDirective.NAME, pathCalculator.getResult());

            OxyTemplateHelper.addDirective(includeDirective, context.getFile());
        }
    }

}
