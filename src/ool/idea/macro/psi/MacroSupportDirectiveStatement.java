package ool.idea.macro.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface MacroSupportDirectiveStatement extends MacroSupportPsiElement
{

    @NotNull
    MacroSupportBlockCloseStatement getBlockCloseStatement();

    @NotNull
    MacroSupportDirectiveOpenStatement getDirectiveOpenStatement();

    @NotNull
    List<MacroSupportDirectiveParamWrapper> getDirectiveParamWrapperList();

}
