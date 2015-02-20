package ool.idea.plugin.editor.highlighter;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CimpiledPreviewHighlighterFactory extends SyntaxHighlighterFactory
{
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable final Project project, @Nullable VirtualFile virtualFile)
    {
        return new OxyTemplateInnerJsHighlighter(JavascriptLanguage.DIALECT_OPTION_HOLDER);
    }

}
