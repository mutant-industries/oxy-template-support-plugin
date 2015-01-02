package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.MacroParamName;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroParamNameImpl extends ASTWrapperPsiElement implements MacroParamName
{

    public MacroParamNameImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroParamName(this);
        else super.accept(visitor);
    }

}
