package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.insert.TrailingPatternConsumer;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 1/31/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroNamespace extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("\\w+:");

    public XmlMacroNamespace()
    {
        extend(CompletionType.BASIC, psiElement(XmlTokenType.XML_NAME).afterSibling(psiElement(XmlTokenType.XML_START_TAG_START)),
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

                    resultSet.consume(PrioritizedLookupElement.withPriority(LookupElementBuilder.create("m:")
                        .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                        {
                            @Override
                            public void handleInsert(InsertionContext context, LookupElement item)
                            {
                                super.handleInsert(context, item);

                                AutoPopupController.getInstance(context.getProject()).autoPopupMemberLookup(context.getEditor(), null);
                            }
                        })
                        .withTypeText("oxy macro namespace", true), Integer.MAX_VALUE)
                    );
                }
            }
        );
    }

}
