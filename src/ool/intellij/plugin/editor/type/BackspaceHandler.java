package ool.intellij.plugin.editor.type;

import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.OxyTemplate;

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition.PARAMETER_QUOTES;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class BackspaceHandler extends BackspaceHandlerDelegate
{
    @Override
    public void beforeCharDeleted(char c, @NotNull PsiFile file, Editor editor)
    {
        int offset = editor.getCaretModel().getOffset();

        if (editor.getDocument().getTextLength() != offset && isBetweenParameterQuotes(file, offset))
        {
            editor.getDocument().deleteString(offset, offset + 1);
        }
    }

    @Override
    public boolean charDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor)
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
