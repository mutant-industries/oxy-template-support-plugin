package ool.idea.plugin.psi;

import org.jetbrains.annotations.Nullable;

public interface DirectiveParamWrapper extends OxyTemplatePsiElement
{
    @Nullable
    DirectiveParamFileReference getDirectiveParamFileReference();

}
