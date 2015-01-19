package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class Directive extends CompletionContributor
{
    public Directive()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_DIRECTIVE).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {

                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {

                    resultSet.addElement(LookupElementBuilder.create("include").withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                    resultSet.addElement(LookupElementBuilder.create("include_once").withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                    resultSet.addElement(LookupElementBuilder.create("layout").withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                }
            }
        );
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
        return typeChar == '@' && position.getNode().getElementType() == OxyTemplateTypes.T_OPEN_BLOCK_MARKER
                    || typeChar == ' ' && position.getNode().getElementType() == OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE;
    }

}
