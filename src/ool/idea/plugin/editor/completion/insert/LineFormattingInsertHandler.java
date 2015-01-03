package ool.idea.plugin.editor.completion.insert;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.codeStyle.CodeStyleManager;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class LineFormattingInsertHandler implements InsertHandler<LookupElement>
{
    @Override
    public void handleInsert(InsertionContext context, LookupElement item)
    {
        Editor editor = context.getEditor();
        CaretModel caretModel = editor.getCaretModel();
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(context.getProject());
        codeStyleManager.adjustLineIndent(context.getFile(), editor.getDocument().getLineStartOffset(caretModel.getLogicalPosition().line));
    }

}
