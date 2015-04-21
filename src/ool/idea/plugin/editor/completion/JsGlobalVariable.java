package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.file.index.globals.JsGlobalsIndex;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import ool.idea.plugin.psi.reference.innerjs.InnerJsReferenceExpressionResolver;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalVariable extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement psiElement = parameters.getPosition();

        if(psiElement.getNode().getElementType() != JSTokenTypes.IDENTIFIER
                || !  InnerJsReferenceExpressionResolver.isGlobalVariableSuspect(psiElement))
        {
            return;
        }

        for (String key : FileBasedIndex.getInstance().getAllKeys(JsGlobalsIndex.INDEX_ID, psiElement.getProject()))
        {
            result.consume(LookupElementBuilder.create(key)
                    .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                    .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
        }
    }

}
