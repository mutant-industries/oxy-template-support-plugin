package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportMacroParameter;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportMacroParameterImpl extends ASTWrapperPsiElement implements MacroSupportMacroParameter
{

    public MacroSupportMacroParameterImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroParameter(this);
        else super.accept(visitor);
    }

}
