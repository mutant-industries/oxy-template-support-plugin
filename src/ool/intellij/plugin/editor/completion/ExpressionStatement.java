package ool.intellij.plugin.editor.completion;

import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ExpressionStatement extends CompletionContributor
{
    @NonNls
    public static final String EXPRESSION_PREFIX = "expr:";

    public ExpressionStatement()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_PARAM),
            new CompletionProvider<CompletionParameters>()
            {

                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    if (parameters.getPosition().getPrevSibling() == null)
                    {
                        resultSet.consume(LookupElementBuilder.create(EXPRESSION_PREFIX + " "));
                    }
                }
            }
        );
    }

}
