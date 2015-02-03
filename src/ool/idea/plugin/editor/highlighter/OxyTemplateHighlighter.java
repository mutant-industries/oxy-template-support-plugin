package ool.idea.plugin.editor.highlighter;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateHighlighter extends LayeredLexerEditorHighlighter
{
    public OxyTemplateHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors)
    {
        super(new OxyTemplateSyntaxHighlighter(/*project*/), colors);

        FileType type = null;

        if ((project == null) || (virtualFile == null))
        {
            type = FileTypes.PLAIN_TEXT;
        }
        else
        {
            Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
            if (language != null) type = language.getAssociatedFileType();
            if (type == null) type = OxyTemplate.getDefaultTemplateLang();
        }

        SyntaxHighlighter markupHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type, project, virtualFile);
        SyntaxHighlighter jsHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(StdFileTypes.JS, project, virtualFile);

        registerLayer(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, new LayerDescriptor(markupHighlighter, "dummy"));
        registerLayer(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, new LayerDescriptor(jsHighlighter, "\n"));
    }

}
