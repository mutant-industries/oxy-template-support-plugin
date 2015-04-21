package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.psi.reference.js.DwrReferenceResolver;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 4/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DwrMethod extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("\\);[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*\\(");

    @NonNls
    private static final String DWR_METHOD_RETURN_TYPE = "void";

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement element = parameters.getPosition();

        if (element.getNode().getElementType() != JSTokenTypes.IDENTIFIER || (element = element.getPrevSibling()) == null)
        {
            return;
        }
        if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
        {
            element = element.getPrevSibling();
        }
        if (element == null || element.getNode().getElementType() != JSTokenTypes.DOT
                || (element = element.getPrevSibling()) == null)
        {
            return;
        }
        if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
        {
            element = element.getPrevSibling();
        }
        if ( ! (element instanceof JSReferenceExpression) || element.getPrevSibling() != null)
        {
            return;
        }

        PsiReference reference = element.getReference();

        if (reference == null || ! ((element = reference.resolve()) instanceof PsiClass)
                || ! DwrReferenceResolver.isDwrClass((PsiClass) element))
        {
            return;
        }

        PsiClass dwrClass = (PsiClass) element;

        for (PsiMethod method : dwrClass.getMethods())
        {
            if ( ! DwrReferenceResolver.isDwrMethod(method))
            {
                continue;
            }

            String presentableText = method.getName() + "()";

            if (method.getPresentation() != null && method.getPresentation().getPresentableText() != null)
            {
                presentableText = method.getPresentation().getPresentableText()
                        .replaceAll("(, )?HttpServlet(Request|Response)", "");
            }

            result.consume(LookupElementBuilder.create(method, method.getName() + "();")
                .withIcon(method.getIcon(0))
                .withTypeText(DWR_METHOD_RETURN_TYPE)
                .withPresentableText(presentableText)
                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                {
                    @Override
                    public void handleInsert(InsertionContext context, LookupElement item)
                    {
                        CaretModel caretModel = context.getEditor().getCaretModel();
                        caretModel.moveToOffset(caretModel.getOffset() - 2);

                        super.handleInsert(context, item);
                    }
                })
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE)
            );
        }

        result.stopHere();
    }

}
