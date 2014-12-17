package ool.idea.macro.psi;

import org.jetbrains.annotations.NotNull;

public interface MacroSupportDirectiveParamWrapper extends MacroSupportPsiElement
{

    @NotNull
    MacroSupportDirectiveParamFileReference getDirectiveParamFileReference();

}
