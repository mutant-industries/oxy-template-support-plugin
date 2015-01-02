package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.BlockCloseStatement;
import ool.idea.plugin.psi.BlockOpenStatement;
import ool.idea.plugin.psi.BlockStatement;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStatementImpl extends ASTWrapperPsiElement implements BlockStatement
{
    public BlockStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitBlockStatement(this);
        else super.accept(visitor);
    }

    @Override
    @Nullable
    public BlockCloseStatement getBlockCloseStatement()
    {
        return findChildByClass(BlockCloseStatement.class);
    }

    @Override
    @NotNull
    public BlockOpenStatement getBlockOpenStatement()
    {
        return findNotNullChildByClass(BlockOpenStatement.class);
    }

}
