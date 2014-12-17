package ool.idea.macro.psi;

import org.jetbrains.annotations.NotNull;

public interface MacroSupportBlockStatement extends MacroSupportPsiElement
{

    @NotNull
    MacroSupportBlockCloseStatement getBlockCloseStatement();

    @NotNull
    MacroSupportBlockOpenStatement getBlockOpenStatement();

}
