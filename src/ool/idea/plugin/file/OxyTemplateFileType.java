package ool.idea.plugin.file;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.Icon;
import ool.idea.plugin.OxyTemplate;
import ool.idea.plugin.highlighter.OxyTemplateTemplateHighlighter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFileType extends LanguageFileType
{
    @NonNls
    private static final Icon DEFAULT_ICON = IconLoader.getIcon("/ool/idea/plugin/icons/default.png");

    @NonNls
    public static final String DEFAULT_EXTENSION = "jsm;jst";

    public static final OxyTemplateFileType INSTANCE = new OxyTemplateFileType();

    private OxyTemplateFileType()
    {
        super(OxyTemplate.INSTANCE);

        // TODO move to component
        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new EditorHighlighterProvider()
        {
            @Override
            public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme editorColorsScheme)
            {
                return new OxyTemplateTemplateHighlighter(project, virtualFile, editorColorsScheme);
            }
        });
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Oxy template";
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Oxy template";
    }

    @NotNull
    @Override
    public String getDefaultExtension()
    {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon()
    {
        return DEFAULT_ICON;
    }
}
