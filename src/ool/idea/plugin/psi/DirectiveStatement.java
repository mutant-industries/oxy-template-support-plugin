package ool.idea.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DirectiveStatement extends OxyTemplatePsiElement
{
    @Nullable
    BlockCloseStatement getBlockCloseStatement();

    @NotNull
    DirectiveOpenStatement getDirectiveOpenStatement();

    @NotNull
    List<DirectiveParamWrapper> getDirectiveParamWrapperList();

}
