package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import ool.idea.plugin.file.index.JavaMacroNameIndex;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroName extends CompletionContributor
{
    public XmlMacroName()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_NAME).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    MacroName elementAt = (MacroName)parameters.getPosition().getParent();

                    if(elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() == OxyTemplateTypes.T_XML_TAG_START)
                    {
                        for(String key : FileBasedIndex.getInstance().getAllKeys(JavaMacroNameIndex.INDEX_ID, elementAt.getProject()))
                        {
                            resultSet.addElement(LookupElementBuilder.create("oxy." + key)
                                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                        }
                    }
                }
            }
        );
    }

}
