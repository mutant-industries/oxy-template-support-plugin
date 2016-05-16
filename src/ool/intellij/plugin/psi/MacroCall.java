package ool.intellij.plugin.psi;

import java.util.List;

import ool.intellij.plugin.psi.macro.param.MacroParamSuggestionSet;

import org.jetbrains.annotations.NotNull;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public interface MacroCall extends OxyTemplateNamedPsiElement
{
    @NotNull
    MacroName getMacroName();

    @NotNull
    List<MacroAttribute> getMacroAttributeList();

    @NotNull
    MacroParamSuggestionSet getParamSuggestionSet();

}
