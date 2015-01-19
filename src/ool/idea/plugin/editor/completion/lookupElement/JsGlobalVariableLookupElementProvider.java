package ool.idea.plugin.editor.completion.lookupElement;

import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalVariableLookupElementProvider implements BaseLookupElementProvider
{
    public static final JsGlobalVariableLookupElementProvider INSTANCE = new JsGlobalVariableLookupElementProvider();

    @NotNull
    @Override
    public LookupElement create(String lookupText)
    {
        return LookupElementBuilder.create(lookupText).withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE);
    }

}
