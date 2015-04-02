package ool.idea.plugin.editor.highlighter;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.usages.impl.SyntaxHighlighterOverEditorHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * 4/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateSyntaxHighlighterFactory extends SyntaxHighlighterFactory
{
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile)
    {
        return new SyntaxHighlighterOverEditorHighlighter(null, virtualFile, project);
    }

}
