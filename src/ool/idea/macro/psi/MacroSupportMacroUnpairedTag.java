package ool.idea.macro.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface MacroSupportMacroUnpairedTag extends MacroSupportPsiElement
{

    @NotNull
    List<MacroSupportMacroAttribute> getMacroAttributeList();

    @NotNull
    MacroSupportMacroXmlPrefix getMacroXmlPrefix();

}
