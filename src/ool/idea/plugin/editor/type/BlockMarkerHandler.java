package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.file.OxyTemplateParserDefinition;
import ool.idea.plugin.lang.OxyTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class BlockMarkerHandler extends TypedHandlerDelegate
{
    @Override
    public TypedHandlerDelegate.Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file)
    {
        PsiElement elementAt;
        FileViewProvider provider = file.getViewProvider();

        if (!(provider instanceof OxyTemplateFileViewProvider))
        {
            return super.charTyped(c, project, editor, file);
        }

        int offset = editor.getCaretModel().getOffset();

        if (offset > editor.getDocument().getTextLength())
        {
            return TypedHandlerDelegate.Result.CONTINUE;
        }

        Pair delimiters = Pair.create("<%", "%>");
        int openBraceLength = ((String) delimiters.first).length();

        if (offset < openBraceLength)
        {
            return TypedHandlerDelegate.Result.CONTINUE;
        }

        String previousChars = editor.getDocument().getText(new TextRange(offset - openBraceLength, offset));

        if (((delimiters.first).equals(previousChars)) && ((elementAt = provider.findElementAt(offset, OxyTemplate.INSTANCE)) == null
                || ! OxyTemplateParserDefinition.INNER_JS.contains(elementAt.getNode().getElementType())))
        {
            if(elementAt != null && elementAt.getText().contains("%>"))
            {
                return TypedHandlerDelegate.Result.CONTINUE;
            }

            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            editor.getDocument().insertString(offset, (CharSequence) delimiters.second);
        }

        return TypedHandlerDelegate.Result.CONTINUE;
    }

}
