package ool.idea.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MacroUnpairedTag extends OxyTemplatePsiElement
{
    @NotNull
    List<MacroAttribute> getMacroAttributeList();

    @Nullable
    MacroName getMacroName();

    @NotNull
    MacroXmlPrefix getMacroXmlPrefix();

}
