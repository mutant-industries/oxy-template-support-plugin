package ool.idea.macro.psi;

import org.jetbrains.annotations.Nullable;

public interface MacroSupportMacroAttribute extends MacroSupportPsiElement
{

    @Nullable
    MacroSupportMacroExpressionParameter getMacroExpressionParameter();

    @Nullable
    MacroSupportMacroParameter getMacroParameter();

}
