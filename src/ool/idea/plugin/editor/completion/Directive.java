package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.psi.DirectiveOpenStatement;
import ool.idea.plugin.psi.DirectiveParamWrapper;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class Directive extends CompletionContributor
{
    private static final String[] DIRECTIVES = {
            "include_once",
            "include",
            "layout"
    };

    private static final Pattern INSERT_CONSUME = Pattern.compile("\"(\\w+)?\\s+\"");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement position = parameters.getPosition(), prevSibling;

        if(position.getNode().getElementType() != OxyTemplateTypes.T_DIRECTIVE)
        {
            return;
        }

        if((prevSibling = position.getPrevSibling()) instanceof PsiWhiteSpace)
        {
            prevSibling = prevSibling.getPrevSibling();
        }

        if( ! (prevSibling instanceof DirectiveOpenStatement))
        {
            return;
        }

        for(String directive : DIRECTIVES)
        {
            result.consume(LookupElementBuilder.create(directive + " \"\"")
                .withPresentableText(directive)
                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                {
                    @Override
                    public void handleInsert(InsertionContext context, LookupElement item)
                    {
                        CaretModel caretModel = context.getEditor().getCaretModel();
                        int offset = caretModel.getOffset();
                        caretModel.moveToOffset(offset - 1);

                        super.handleInsert(context, item);

                        PsiElement elementAt = context.getFile().getViewProvider().findElementAt(offset - 1);

                        if (elementAt != null && elementAt.getParent() instanceof DirectiveParamWrapper) // redundant np check
                        {
                            if (((DirectiveParamWrapper) elementAt.getParent()).getDirectiveParamFileReference() == null)
                            {
                                AutoPopupController.getInstance(context.getProject()).autoPopupMemberLookup(context.getEditor(), null);
                            }
                        }

                    }
                })
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
        }
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
        return typeChar == ' ' && position.getNode().getElementType() == OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE;
    }

}
