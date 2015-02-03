package ool.idea.plugin.editor.completion.insert;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1/31/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class TrailingPatternConsumer implements InsertHandler<LookupElement>
{
    private final Pattern pattern;

    public TrailingPatternConsumer(Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public void handleInsert(InsertionContext context, LookupElement item)
    {
        Document document = context.getDocument();

        CaretModel caretModel = context.getEditor().getCaretModel();
        int offset = caretModel.getOffset();

        if(offset == document.getTextLength() - 1)
        {
            return;
        }

        String text = context.getDocument().getText(TextRange.create(offset, document.getTextLength()));

        Matcher matcher = pattern.matcher(text);

        if(matcher.find() && matcher.start() == 0)
        {
            document.replaceString(offset, offset + matcher.end(), "");
        }

        context.commitDocument();
    }

}
