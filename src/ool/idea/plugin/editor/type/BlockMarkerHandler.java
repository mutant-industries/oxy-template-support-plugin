package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class BlockMarkerHandler extends TypedHandlerDelegate
{
    @Override
    public Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file)
    {
        PsiElement elementAt;
        FileViewProvider provider = file.getViewProvider();

        if (!(provider instanceof OxyTemplateFileViewProvider))
        {
            return Result.CONTINUE;
        }

        int offset = editor.getCaretModel().getOffset();

        // <% _%> -> <% _ %>
        if(c == ' ' && (elementAt = file.getViewProvider().findElementAt(offset, OxyTemplate.INSTANCE)) != null
                && elementAt.getNode().getElementType() == OxyTemplateTypes.T_CLOSE_BLOCK_MARKER
                && OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(elementAt.getParent().getPrevSibling().getNode().getElementType()))
        {
            editor.getDocument().insertString(offset, " ");
            editor.getCaretModel().moveToOffset(offset);

            return Result.CONTINUE;
        }

        Pair delimiters = Pair.create("<%", "%>");
        int openBraceLength = ((String) delimiters.first).length();

        if (offset < openBraceLength)
        {
            return Result.CONTINUE;
        }

        String previousChars = editor.getDocument().getText(new TextRange(offset - openBraceLength, offset));

        // <%_ -> <%_%>
        if (((delimiters.first).equals(previousChars)) && ((elementAt = provider.findElementAt(offset, OxyTemplate.INSTANCE)) == null
                || ! OxyTemplateParserDefinition.INNER_JS.contains(elementAt.getNode().getElementType())))
        {
            if(elementAt != null && elementAt.getText().contains("%>"))
            {
                return Result.CONTINUE;
            }

            editor.getDocument().insertString(offset, delimiters.second.toString());
        }

        return Result.CONTINUE;
    }

}
