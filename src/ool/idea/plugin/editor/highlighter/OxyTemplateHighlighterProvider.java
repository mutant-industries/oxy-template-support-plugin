package ool.idea.plugin.editor.highlighter;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/10/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateHighlighterProvider implements EditorHighlighterProvider
{
    @Override
    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors)
    {
        return new OxyTemplateHighlighter(project, virtualFile, colors);
    }

}
