package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.MacroParam;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroParamImpl extends ASTWrapperPsiElement implements MacroParam
{
    public MacroParamImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroParam(this);
        else super.accept(visitor);
    }

}
