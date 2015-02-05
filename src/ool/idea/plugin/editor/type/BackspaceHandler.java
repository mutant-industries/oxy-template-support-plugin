package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.lang.OxyTemplate;
import static ool.idea.plugin.lang.parser.OxyTemplateParserDefinition.PARAMETER_QUOTES;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class BackspaceHandler extends BackspaceHandlerDelegate
{
    @Override
    public void beforeCharDeleted(char c, PsiFile file, Editor editor)
    {
        int offset = editor.getCaretModel().getOffset();

        if (editor.getDocument().getTextLength() != offset && c == '"' && isBetweenParameterQuotes(file, offset))
        {
            editor.getDocument().deleteString(offset, offset + 1);
        }
    }

    @Override
    public boolean charDeleted(char c, PsiFile file, Editor editor)
    {
        return true;
    }

    /**
     * @param file
     * @param offset
     * @return
     */
    private static boolean isBetweenParameterQuotes(PsiFile file, int offset)
    {
        FileViewProvider provider = file.getViewProvider();

        if ( ! (provider instanceof OxyTemplateFileViewProvider))
        {
            return false;
        }

        return isParameterQuote(provider.findElementAt(offset - 1, OxyTemplate.INSTANCE))
                || isParameterQuote(provider.findElementAt(offset - 1, HTMLLanguage.INSTANCE));
    }

    /**
     * @param element
     * @return
     */
    private static boolean isParameterQuote(@Nullable PsiElement element)
    {
        return element != null && PARAMETER_QUOTES.contains(element.getNode().getElementType())
                && (element = element.getNextSibling()) != null
                && (element instanceof PsiErrorElement || PARAMETER_QUOTES.contains(element.getNode().getElementType()));
    }

}
