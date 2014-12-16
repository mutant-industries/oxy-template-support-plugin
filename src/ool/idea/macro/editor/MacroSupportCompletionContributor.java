package ool.idea.macro.editor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mayrp on 12/15/14.
 */
public class MacroSupportCompletionContributor extends CompletionContributor
{
    public MacroSupportCompletionContributor()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(MacroSupportTypes.DIRECTIVE).withLanguage(MacroSupport.INSTANCE),
                new CompletionProvider<CompletionParameters>()
                {

                    @Override
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet)
                    {

                        resultSet.addElement(LookupElementBuilder.create("include"));
                        resultSet.addElement(LookupElementBuilder.create("include_once"));
                        resultSet.addElement(LookupElementBuilder.create("layout"));
                    }
                }
        );
    }
}
