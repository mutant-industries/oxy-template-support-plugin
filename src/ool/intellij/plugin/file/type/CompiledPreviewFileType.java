package ool.intellij.plugin.file.type;

import javax.swing.*;

import ool.intellij.plugin.lang.CompiledPreview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewFileType extends LanguageFileType
{
    public static final CompiledPreviewFileType INSTANCE = new CompiledPreviewFileType();

    private CompiledPreviewFileType()
    {
        super(CompiledPreview.INSTANCE);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "CompiledPreview";
    }

    @NonNls
    @NotNull
    @Override
    public String getDescription()
    {
        return "Live preview of compiled oxy template";
    }

    @NotNull
    @Override
    public String getDefaultExtension()
    {
        return "compiled";
    }

    @Nullable
    @Override
    public Icon getIcon()
    {
        return AllIcons.FileTypes.JavaScript;
    }

}
