package ool.idea.plugin.editor.completion.lookupElement;

import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import ool.idea.plugin.file.OxyTemplateFileType;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroNameLookupElementProvider implements BaseLookupElementProvider
{
    public static final XmlMacroNameLookupElementProvider INSTANCE = new XmlMacroNameLookupElementProvider();

    @NotNull
    @Override
    public LookupElement create(String lookupText, @NotNull Object lookupObject)
    {
        return LookupElementBuilder.create(lookupObject, lookupText).withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE);
    }

}
