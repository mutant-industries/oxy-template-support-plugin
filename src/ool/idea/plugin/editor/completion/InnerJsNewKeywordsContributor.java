package ool.idea.plugin.editor.completion;

import com.intellij.lang.javascript.completion.JSCompletionKeywordsContributor;
import com.intellij.lang.javascript.completion.KeywordCompletionConsumer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;

/**
 * 2/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsNewKeywordsContributor extends JSCompletionKeywordsContributor
{
    @NonNls
    public static final String EACH = "each";

    @Override
    public void appendSpecificKeywords(KeywordCompletionConsumer consumer)
    {
        consumer.consume(3, true, EACH);
    }

    @Override
    public boolean process(KeywordCompletionConsumer consumer, PsiElement context)
    {
        return true;
    }

}
