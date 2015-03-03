package ool.idea.plugin.editor.completion.handler;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.InsertHandlerDecorator;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.lang.javascript.psi.JSElement;
import ool.idea.plugin.file.RelativePathCalculator;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.web.template.impl.chunk.directive.IncludeOnceDirective;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludeAutoInsert implements InsertHandlerDecorator<LookupElement>
{
    @Override
    public void handleInsert(InsertionContext context, LookupElementDecorator<LookupElement> item)
    {
        item.getDelegate().handleInsert(context);

        if( ! (item.getObject() instanceof JSElement))
        {
            return;
        }

        JSElement macroDefinition = (JSElement)item.getObject();

        if(OxyTemplateHelper.isJsMacroMissingInclude(context.getFile().getViewProvider().getPsi(OxyTemplate.INSTANCE), macroDefinition))
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
