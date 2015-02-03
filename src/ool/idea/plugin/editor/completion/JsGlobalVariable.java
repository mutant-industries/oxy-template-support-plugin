package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSProperty;
import static com.intellij.patterns.PlatformPatterns.not;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.insert.TrailingPatternConsumer;
import ool.idea.plugin.file.index.globals.JsGlobalsIndex;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalVariable extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*");

    public JsGlobalVariable()
    {
        extend(CompletionType.BASIC, psiElement(JSTokenTypes.IDENTIFIER).withParent(not(psiElement(JSProperty.class))),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    PsiElement psiElement = parameters.getPosition();

                    for (String key : FileBasedIndex.getInstance().getAllKeys(JsGlobalsIndex.INDEX_ID, psiElement.getProject()))
                    {
                        resultSet.consume(LookupElementBuilder.create(key)
                                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                    }
                }
            }
        );
    }

}
