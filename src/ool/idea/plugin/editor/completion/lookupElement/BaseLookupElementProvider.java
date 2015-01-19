package ool.idea.plugin.editor.completion.lookupElement;

import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public interface BaseLookupElementProvider
{
    @NotNull
    public LookupElement create(String lookupText);

}
