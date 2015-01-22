package ool.idea.plugin.editor.completion.insert;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.javascript.psi.JSElement;
import ool.idea.plugin.file.RelativePathCalculator;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.impl.OxyTemplatePsiUtil;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludeAutoInsertHandler implements InsertHandler<LookupElement>
{
    @Override
    public void handleInsert(InsertionContext context, LookupElement item)
    {
        if( ! (item.getObject() instanceof JSElement))
        {
            return;
        }

        JSElement macro = (JSElement)item.getObject();

        if(OxyTemplatePsiUtil.isJsMacroMissingInclude(context.getFile(), macro))
        {
            RelativePathCalculator pathCalculator = new RelativePathCalculator(context.getFile().getVirtualFile().getPath(),
                    macro.getContainingFile().getVirtualFile().getPath());
            pathCalculator.execute();

            DirectiveStatement includeDirective = OxyTemplateElementFactory
                    .createDirectiveStatement(context.getProject(), "include_once", pathCalculator.getResult());

//            context.getFile().addBefore(context.getFile().getFirstChild(), includeDirective);
            context.getEditor().getDocument().insertString(0, includeDirective.getText() + "\n");
        }
    }

}
