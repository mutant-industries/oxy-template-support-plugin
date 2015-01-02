package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.BlockOpenStatement;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class BlockOpenStatementImpl extends ASTWrapperPsiElement implements BlockOpenStatement
{
    public BlockOpenStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitBlockOpenStatement(this);
        else super.accept(visitor);
    }

}
