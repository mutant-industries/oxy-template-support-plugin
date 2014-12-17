package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportMacroExpressionParameter;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportMacroExpressionParameterImpl extends ASTWrapperPsiElement implements MacroSupportMacroExpressionParameter
{

    public MacroSupportMacroExpressionParameterImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroExpressionParameter(this);
        else super.accept(visitor);
    }

}
