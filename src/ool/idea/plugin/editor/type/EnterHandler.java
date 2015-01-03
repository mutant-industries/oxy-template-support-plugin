package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.OxyTemplate;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
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
        if (isBetweenMacroTags(file, caretOffset.get()))
        {
            originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);

            return Result.Default;
        }

        return Result.Continue;
    }

    /**
     *
     * @param file
     * @param offset
     * @return
     */
    private static boolean isBetweenMacroTags(PsiFile file, int offset)
    {
        FileViewProvider provider = file.getViewProvider();

        if ( ! (provider instanceof OxyTemplateFileViewProvider))
        {
            return false;
        }

        PsiElement element = provider.findElementAt(offset, OxyTemplate.INSTANCE);

        return element != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START
                && (element = element.getPrevSibling()) != null && element.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END;
    }

}
