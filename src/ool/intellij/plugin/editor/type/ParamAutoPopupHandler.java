package ool.intellij.plugin.editor.type;

import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * 2/19/19
 *
 * @author Mutant Industries ltd. <mutant-industries@gmx.com>
 */
public class ParamAutoPopupHandler extends TypedHandlerDelegate
{
    @NotNull
    @Override
    public Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file)
    {
        PsiElement elementAt;
        int offset = editor.getCaretModel().getOffset();

        if (charTyped == ' ' && offset > 0 && (elementAt = file.findElementAt(offset - 1)) != null)
        {
            IElementType elementType = elementAt.getNode().getElementType();

            if (elementType == OxyTemplateTypes.T_MACRO_NAME || elementType == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY
                    && elementAt.getParent().getLastChild().isEquivalentTo(elementAt))
            {
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
            }
        }

        return Result.CONTINUE;
    }

}
