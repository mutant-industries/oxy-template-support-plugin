package ool.idea.macro.file;

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
import ool.idea.macro.MacroSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportFileType extends LanguageFileType
{
    @NonNls
    private static final Icon DEFAULT_ICON = IconLoader.getIcon("/ool/idea/macro/icons/default.png");

    @NonNls
    public static final String DEFAULT_EXTENSION = "jsm";

    public static final MacroSupportFileType INSTANCE = new MacroSupportFileType();

    private MacroSupportFileType()
    {
        super(MacroSupport.INSTANCE);

        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new EditorHighlighterProvider()
        {
            @Override
            public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme editorColorsScheme)
            {
                return new MacroSupportTemplateHighlighter(project, virtualFile, editorColorsScheme);
            }
        });
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Oxy macro";
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Každý je zná";
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
