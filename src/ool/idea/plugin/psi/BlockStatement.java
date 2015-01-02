package ool.idea.plugin.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockStatement extends OxyTemplatePsiElement
{
    @Nullable
    BlockCloseStatement getBlockCloseStatement();

    @NotNull
    BlockOpenStatement getBlockOpenStatement();

}
