package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.macro.psi.MacroSupportBlockCloseStatement;
import ool.idea.macro.psi.MacroSupportDirectiveOpenStatement;
import ool.idea.macro.psi.MacroSupportDirectiveParamWrapper;
import ool.idea.macro.psi.MacroSupportDirectiveStatement;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportDirectiveStatementImpl extends ASTWrapperPsiElement implements MacroSupportDirectiveStatement
{

    public MacroSupportDirectiveStatementImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitDirectiveStatement(this);
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
    public MacroSupportDirectiveOpenStatement getDirectiveOpenStatement()
    {
        return findNotNullChildByClass(MacroSupportDirectiveOpenStatement.class);
    }

    @Override
    @NotNull
    public List<MacroSupportDirectiveParamWrapper> getDirectiveParamWrapperList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportDirectiveParamWrapper.class);
    }

}
