package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.editor.type.TagCloseHandler;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnclosedMacroTag extends CompletionContributor
{
    public UnclosedMacroTag()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_NAME_IDENTIFIER).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    MacroName elementAt = PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroName.class);

                    if(elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START)
                    {
                        String macroTagToBeClosedName = TagCloseHandler.getPreviousUnclosedMacroTagName(elementAt.getPrevSibling());

                        if (macroTagToBeClosedName != null)
                        {
                            resultSet.addElement(LookupElementBuilder.create(macroTagToBeClosedName + ">")
                                    .withPresentableText("m:" + macroTagToBeClosedName)
//                                    .withInsertHandler(new LineFormattingInsertHandler())
                                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                        }
                    }
                }
            }
        );
    }

}
