package ool.idea.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public interface MacroCall extends OxyTemplatePsiElement
{
    @Nullable
    MacroName getMacroName();

    @NotNull
    List<MacroAttribute> getMacroAttributeList();

}
