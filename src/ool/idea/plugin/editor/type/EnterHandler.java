package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.parser.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.BlockCloseStatement;
import ool.idea.plugin.psi.BlockOpenStatement;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class EnterHandler extends EnterHandlerDelegateAdapter
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

        if ((file.getViewProvider() instanceof OxyTemplateFileViewProvider)
            && (isBetweenMacroTags(elementAt) || isBetweenBlockMarkers(elementAt)))
        {
            originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);

            return Result.Default;
        }

        return Result.Continue;
    }

    private static boolean isBetweenMacroTags(@Nullable PsiElement element)
    {
        return element != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START
                && (element = element.getPrevSibling()) != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END;
    }

    private static boolean isBetweenBlockMarkers(@Nullable PsiElement element)
    {
        if(element == null)
        {
            return false;
        }

        IElementType elementType = element.getNode().getElementType();

        if(OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(elementType))
        {
            element = element.getParent().getPrevSibling();

            if(element instanceof PsiWhiteSpace)
            {
                element = element.getPrevSibling();
            }

            return element != null && OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(element.getNode().getElementType());
        }
        else if(OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(elementType))
        {
            element = element.getParent().getNextSibling();

            if(element instanceof PsiWhiteSpace)
            {
                element = element.getNextSibling();
            }

            return element != null && OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(element.getNode().getElementType());
        }
        else if(element instanceof PsiWhiteSpace)
        {
            return element.getNextSibling() instanceof BlockCloseStatement && element.getPrevSibling() instanceof BlockOpenStatement
                    && ! element.textContains('\n');
        }

        return false;
    }

}
