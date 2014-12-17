package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportBlockOpenStatement;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportBlockOpenStatementImpl extends ASTWrapperPsiElement implements MacroSupportBlockOpenStatement
{

    public MacroSupportBlockOpenStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitBlockOpenStatement(this);
        else super.accept(visitor);
    }

}
