package ool.idea.plugin.psi;

import java.util.List;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
import org.jetbrains.annotations.NotNull;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public interface MacroCall extends OxyTemplateNamedPsiElement
{
    @NotNull
    public MacroName getMacroName();

    @NotNull
    public List<MacroAttribute> getMacroAttributeList();

    @NotNull
    public MacroParamSuggestionSet getParamSuggestionSet();

}
