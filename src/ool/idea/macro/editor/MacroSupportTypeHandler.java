package ool.idea.macro.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.editorActions.XmlGtTypedHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.TokenSet;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.file.MacroSupportFileViewProvider;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mayrp on 12/15/14.
 */
public class MacroSupportTypeHandler extends XmlGtTypedHandler
{
    @Override
    public TypedHandlerDelegate.Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file)
    {
        PsiElement elementAt;
        FileViewProvider provider = file.getViewProvider();

        if (!(provider instanceof MacroSupportFileViewProvider))
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

        if (((delimiters.first).equals(previousChars)) && ((elementAt = provider.findElementAt(offset, MacroSupport.INSTANCE)) == null
                || ! TokenSet.create(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE).contains(elementAt.getNode().getElementType())))
        {
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            editor.getDocument().insertString(offset, (CharSequence) delimiters.second);
        }

        return TypedHandlerDelegate.Result.CONTINUE;
    }

}
