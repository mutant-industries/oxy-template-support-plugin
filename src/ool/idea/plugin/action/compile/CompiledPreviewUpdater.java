package ool.idea.plugin.action.compile;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.ui.update.Update;
import ool.idea.plugin.lang.CompiledPreview;
import org.jetbrains.annotations.NotNull;

/**
 * 2/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewUpdater extends Update
{
    private final Project project;

    private final CompiledPreviewController previewController;

    public CompiledPreviewUpdater(@NotNull Object identity, @NotNull CompiledPreviewController previewController, @NotNull Project project)
    {
        super(identity, true);

        this.project = project;
        this.previewController = previewController;
    }

    @Override
    public void run()
    {
        for (FileEditor fileEditor : FileEditorManager.getInstance(project).getAllEditors())
        {
            if ( ! (fileEditor instanceof TextEditor))
            {
                continue;
            }

            EditorEx editor = (EditorEx) ((TextEditor) fileEditor).getEditor();

            VirtualFile virtualFile = editor.getVirtualFile();
            Language language = virtualFile instanceof LightVirtualFile ? ((LightVirtualFile) virtualFile).getLanguage() : null;

            if (language != CompiledPreview.INSTANCE)
            {
                continue;
            }

            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            PsiFile originalFile;

            if (psiFile == null || (originalFile = psiFile.getUserData(CompiledPreviewController.OFIGINAL_FILE_KEY)) == null)
            {
                continue;
            }

            previewController.showCompiledCode(originalFile, editor.getDocument());
        }
    }

}
