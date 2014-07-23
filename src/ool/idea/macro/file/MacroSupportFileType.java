package ool.idea.macro.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
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
    private static final MacroSupportFileType INSTANCE = new MacroSupportFileType();

    @NonNls
    public static final String DEFAULT_EXTENSION = "jsm";

    @NonNls
    private static final Icon DEFAULT_ICON = IconLoader.getIcon("/ool/idea/macro/icons/default.png");

    private MacroSupportFileType()
    {
        super(MacroSupport.getInstance());
    }

    public static MacroSupportFileType getInstance()
    {
        return INSTANCE;
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
