package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroNameImpl extends ASTWrapperPsiElement implements MacroName
{
    public MacroNameImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroName(this);
        else super.accept(visitor);
    }

}
