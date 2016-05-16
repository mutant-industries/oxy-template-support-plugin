package ool.intellij.plugin.file.type;

import javax.swing.*;

import ool.intellij.plugin.lang.OxyTemplate;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
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
    private static final Icon DEFAULT_ICON = IconLoader.getIcon("/icons/default.png");

    @NonNls
    public static final String DEFAULT_EXTENSION = "jsm;jst";

    public static final OxyTemplateFileType INSTANCE = new OxyTemplateFileType();

    private OxyTemplateFileType()
    {
        super(OxyTemplate.INSTANCE);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "OxyTemplate";
    }

    @NonNls
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
