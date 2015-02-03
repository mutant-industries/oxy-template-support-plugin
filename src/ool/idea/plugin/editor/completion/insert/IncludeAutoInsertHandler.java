package ool.idea.plugin.editor.completion.insert;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.InsertHandlerDecorator;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.file.RelativePathCalculator;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.impl.OxyTemplatePsiUtil;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludeAutoInsertHandler implements InsertHandlerDecorator<LookupElement>
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

        if(OxyTemplatePsiUtil.isJsMacroMissingInclude(context.getFile().getViewProvider().getPsi(OxyTemplate.INSTANCE), macroDefinition))
        {
            RelativePathCalculator pathCalculator = new RelativePathCalculator(context.getFile().getVirtualFile().getPath(),
                    macroDefinition.getContainingFile().getVirtualFile().getPath());
            pathCalculator.execute();

            DirectiveStatement includeDirective = OxyTemplateElementFactory
                    .createDirectiveStatement(context.getProject(), "include_once", pathCalculator.getResult());

            List<DirectiveStatement> statements = PsiTreeUtil.getChildrenOfTypeAsList(context.getFile(), DirectiveStatement.class);

            /** TODO duplicate code {@link ool.idea.plugin.editor.inspection.fix.MissingIncludeDirectiveQuickFix#applyFix(Project)} */
            if(statements.size() > 0)
            {
                context.getFile().addAfter(includeDirective, statements.get(statements.size() - 1));
            }
            else
            {
                context.getFile().addBefore(includeDirective, context.getFile().getFirstChild());
            }
            // ----------------------------------------------------------------------------
        }
    }

}
