package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.psi.BlockCloseStatement;
import ool.idea.plugin.psi.DirectiveOpenStatement;
import ool.idea.plugin.psi.DirectiveParamWrapper;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectiveStatementImpl extends ASTWrapperPsiElement implements DirectiveStatement
{
    public DirectiveStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitDirectiveStatement(this);
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
    public DirectiveOpenStatement getDirectiveOpenStatement()
    {
        return findNotNullChildByClass(DirectiveOpenStatement.class);
    }

    @Override
    @NotNull
    public List<DirectiveParamWrapper> getDirectiveParamWrapperList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, DirectiveParamWrapper.class);
    }

}
