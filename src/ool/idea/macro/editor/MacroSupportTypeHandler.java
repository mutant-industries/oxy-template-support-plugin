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
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.file.MacroSupportFileViewProvider;
import ool.idea.macro.psi.MacroSupportBlockStatement;
import ool.idea.macro.psi.MacroSupportPsiElement;
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
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        if (((delimiters.first).equals(previousChars))/* &&
                ! hasClosingBrace(project, editor, provider, offset - openBraceLength / 2)*/)
        {
            editor.getDocument().insertString(offset, (CharSequence) delimiters.second);
        }

        return TypedHandlerDelegate.Result.CONTINUE;
    }

//    private boolean hasClosingBrace(Project project, Editor editor, FileViewProvider provider, int offset)
//    {
//        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
//        MacroSupportPsiElement elementAt = PsiTreeUtil.getParentOfType(
//                provider.findElementAt(offset, MacroSupport.INSTANCE), MacroSupportPsiElement.class);
//
//        PsiElement parent = elementAt != null ? elementAt.getParent() : null;
//
//        return ((parent instanceof MacroSupportBlockStatement))
//                && (((MacroSupportBlockStatement) parent).getBlockOpenStatement() == elementAt)
//                && (!containsUnbalancedOpenBrace((MacroSupportBlockStatement) parent));
//    }
//
//    private boolean containsUnbalancedOpenBrace(MacroSupportBlockStatement braces)
//    {
//        PsiElement currentChild = braces.getBlockOpenStatement();
//        if (currentChild == null) return false;
//        while ((currentChild = currentChild.getNextSibling()) != null)
//        {
//            if (((currentChild instanceof MacroSupportPsiElement))
//                    && (MacroSupportTypes.BLOCK_OPEN_STATEMENT == ((MacroSupportPsiElement) currentChild)))
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }

}
