package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.BlockStatement;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

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
        if ((file.getViewProvider() instanceof OxyTemplateFileViewProvider)
            && (isBetweenMacroTags(file.getViewProvider(), caretOffset.get()) || isBetweenBlockMarkers(file.getViewProvider(), caretOffset.get())))
        {
            originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);

            return Result.Default;
        }

        return Result.Continue;
    }

    private static boolean isBetweenMacroTags(FileViewProvider provider, int offset)
    {
        PsiElement element = provider.findElementAt(offset, OxyTemplate.INSTANCE);

        return element != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START
                && (element = element.getPrevSibling()) != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END;
    }

    private static boolean isBetweenBlockMarkers(FileViewProvider provider, int offset)
    {
        PsiElement element = provider.findElementAt(offset, OxyTemplate.INSTANCE);
        BlockStatement blockStatement;

        return (blockStatement = PsiTreeUtil.getParentOfType(element, BlockStatement.class)) != null
                && (element = blockStatement.getBlockOpenStatement().getNextSibling()) != null
                && element.getNode().getElementType() != OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE;

    }

}
