package ool.idea.plugin.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MacroAttribute extends OxyTemplatePsiElement
{
    @Nullable
    MacroExpressionParam getMacroExpressionParam();

    @Nullable
    MacroParam getMacroParam();

    @NotNull
    MacroParamName getMacroParamName();

}
