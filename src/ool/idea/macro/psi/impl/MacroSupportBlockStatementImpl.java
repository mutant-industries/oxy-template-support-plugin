package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportBlockCloseStatement;
import ool.idea.macro.psi.MacroSupportBlockOpenStatement;
import ool.idea.macro.psi.MacroSupportBlockStatement;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportBlockStatementImpl extends ASTWrapperPsiElement implements MacroSupportBlockStatement
{

    public MacroSupportBlockStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitBlockStatement(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public MacroSupportBlockCloseStatement getBlockCloseStatement()
    {
        return findNotNullChildByClass(MacroSupportBlockCloseStatement.class);
    }

    @Override
    @NotNull
    public MacroSupportBlockOpenStatement getBlockOpenStatement()
    {
        return findNotNullChildByClass(MacroSupportBlockOpenStatement.class);
    }

}
