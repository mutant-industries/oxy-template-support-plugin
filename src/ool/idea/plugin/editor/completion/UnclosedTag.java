package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.StdLanguages;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.OxyTemplate;
import ool.idea.plugin.editor.completion.insert.LineFormattingInsertHandler;
import ool.idea.plugin.editor.type.TagCloseHandler;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 1/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnclosedTag extends CompletionContributor
{
    public UnclosedTag()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(XmlTokenType.XML_NAME),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    FileViewProvider provider = parameters.getOriginalFile().getViewProvider();

                    if (!(provider instanceof OxyTemplateFileViewProvider))
                    {
                        return;
                    }

                    int offset = parameters.getOffset();

                    PsiElement elementAt = provider.findElementAt(offset - 1, OxyTemplate.INSTANCE);
                    PsiElement htmlElementAt = provider.findElementAt(offset - 1, StdLanguages.HTML);

                    if(htmlElementAt.getNode().getElementType() == XmlTokenType.XML_END_TAG_START)
                    {
                        String macroTagToBeClosedName = TagCloseHandler.getPreviousUnclosedMacroTagName(elementAt);

                        if(macroTagToBeClosedName != null)
                        {
                            resultSet.addElement(LookupElementBuilder.create("m:" + macroTagToBeClosedName + ">")
                                    .withPresentableText("m:" + macroTagToBeClosedName)
                                    .withInsertHandler(new LineFormattingInsertHandler())
                                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                        }
                    }
                    else
                    {
                        resultSet.addElement(LookupElementBuilder.create("m:"));
                    }
                }
            }
        );
    }

}
