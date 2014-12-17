package ool.idea.macro.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface MacroSupportMacroTag extends MacroSupportPsiElement
{

    @NotNull
    List<MacroSupportBlockStatement> getBlockStatementList();

    @NotNull
    List<MacroSupportMacroAttribute> getMacroAttributeList();

    @NotNull
    List<MacroSupportMacroTag> getMacroTagList();

    @NotNull
    List<MacroSupportMacroUnpairedTag> getMacroUnpairedTagList();

    @NotNull
    List<MacroSupportMacroXmlPrefix> getMacroXmlPrefixList();

}
