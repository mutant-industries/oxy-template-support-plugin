package ool.intellij.plugin.editor.type;

import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.BlockCloseStatement;
import ool.intellij.plugin.psi.BlockOpenStatement;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class EnterHandler extends EnterBetweenBracesHandler
{
    @Override
    public Result preprocessEnter(@NotNull final PsiFile file,
                                  @NotNull final Editor editor,
                                  @NotNull final Ref<Integer> caretOffset,
                                  @NotNull final Ref<Integer> caretAdvance,
                                  @NotNull final DataContext dataContext,
                                  final EditorActionHandler originalHandler)
    {
        PsiElement elementAt = file.getViewProvider().findElementAt(caretOffset.get(), OxyTemplate.INSTANCE);

        if ((file.getViewProvider() instanceof OxyTemplateFileViewProvider))
        {
            if (isBetweenMacroTags(elementAt))
            {
                originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
            }
            else if (isBetweenBlockMarkers(elementAt, caretOffset.get()))
            {
                originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);

                PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());

                CaretModel caretModel = editor.getCaretModel();
                CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(file.getProject());
                codeStyleManager.adjustLineIndent(file, editor.getDocument().getLineStartOffset(caretModel.getLogicalPosition().line));
            }

            return super.preprocessEnter(file, editor, caretOffset, caretAdvance, dataContext, originalHandler);
        }

        return Result.Continue;
    }

    private static boolean isBetweenMacroTags(@Nullable PsiElement element)
    {
        return element != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START
                && (element = element.getPrevSibling()) != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END;
    }

    private static boolean isBetweenBlockMarkers(@Nullable PsiElement element, int offset)
    {
        if (element == null)
        {
            return false;
        }

        IElementType elementType = element.getNode().getElementType();

        if (OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(elementType))
        {
            if (offset > element.getNode().getStartOffset())
            {
                return false;
            }

            element = element.getParent().getPrevSibling();

            if (element instanceof PsiWhiteSpace)
            {
                if (element.textContains('\n'))
                {
                    return false;
                }

                element = element.getPrevSibling();
            }

            return element != null && OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(element.getNode().getElementType());
        }
        else if (OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(elementType))
        {
            if (offset < element.getNode().getStartOffset() + element.getTextLength())
            {
                return false;
            }

            element = element.getParent().getNextSibling();

            if (element instanceof PsiWhiteSpace)
            {
                if (element.textContains('\n'))
                {
                    return false;
                }

                element = element.getNextSibling();
            }

            return element != null && OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(element.getNode().getElementType());
        }
        else if (element instanceof PsiWhiteSpace)
        {
            return element.getNextSibling() instanceof BlockCloseStatement && element.getPrevSibling() instanceof BlockOpenStatement
                    && ! element.textContains('\n');
        }

        return false;
    }

    @Override
    protected boolean isBracePair(char c1, char c2)
    {
        return c1 == '{' && c2 == '}';
    }

}
