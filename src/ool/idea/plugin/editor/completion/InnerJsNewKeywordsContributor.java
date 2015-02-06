package ool.idea.plugin.editor.completion;

import com.intellij.lang.javascript.completion.JSCompletionKeywordsContributor;
import com.intellij.lang.javascript.completion.KeywordCompletionConsumer;
import com.intellij.psi.PsiElement;

/**
 * 2/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsNewKeywordsContributor extends JSCompletionKeywordsContributor
{
    @Override
    public void appendSpecificKeywords(KeywordCompletionConsumer consumer)
    {
        consumer.consume(3, true, "each");
    }

    @Override
    public boolean process(KeywordCompletionConsumer consumer, PsiElement context)
    {
        return true;
    }

}
