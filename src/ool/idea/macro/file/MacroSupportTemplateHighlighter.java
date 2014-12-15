package ool.idea.macro.file;

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
import ool.idea.macro.MacroSupport;
import ool.idea.macro.highlighter.MacroSupportHighlighter;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportTemplateHighlighter extends LayeredLexerEditorHighlighter
{
    public MacroSupportTemplateHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors)
    {
        super(new MacroSupportHighlighter(/*project*/), colors);

        FileType type = null;
        if ((project == null) || (virtualFile == null))
        {
            type = FileTypes.PLAIN_TEXT;
        }
        else
        {
            Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
            if (language != null) type = language.getAssociatedFileType();
            if (type == null) type = MacroSupport.getDefaultTemplateLang();
        }

        SyntaxHighlighter customHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(MacroSupport.INSTANCE, project, virtualFile);
        SyntaxHighlighter outerHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type, project, virtualFile);
        SyntaxHighlighter innerHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(StdFileTypes.JS, project, virtualFile);

        registerLayer(MacroSupportTypes.DIRECTIVE_STATEMENT, new LayerDescriptor(customHighlighter, ""));
        registerLayer(MacroSupportTypes.MACRO_TAG, new LayerDescriptor(customHighlighter, ""));
        registerLayer(MacroSupportTypes.TEMPLATE_HTML_CODE, new LayerDescriptor(outerHighlighter, ""));
        registerLayer(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, new LayerDescriptor(innerHighlighter, ""));
    }
}
