package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportDirectiveParamFileReference;
import ool.idea.macro.psi.MacroSupportDirectiveParamWrapper;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportDirectiveParamWrapperImpl extends ASTWrapperPsiElement implements MacroSupportDirectiveParamWrapper
{

    public MacroSupportDirectiveParamWrapperImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitDirectiveParamWrapper(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public MacroSupportDirectiveParamFileReference getDirectiveParamFileReference()
    {
        return findNotNullChildByClass(MacroSupportDirectiveParamFileReference.class);
    }

}
