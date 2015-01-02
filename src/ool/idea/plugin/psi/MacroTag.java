package ool.idea.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface MacroTag extends OxyTemplatePsiElement
{
    @NotNull
    List<BlockStatement> getBlockStatementList();

    @NotNull
    List<MacroAttribute> getMacroAttributeList();

    @NotNull
    List<MacroName> getMacroNameList();

    @NotNull
    List<MacroTag> getMacroTagList();

    @NotNull
    List<MacroUnpairedTag> getMacroUnpairedTagList();

    @NotNull
    List<MacroXmlPrefix> getMacroXmlPrefixList();

}
