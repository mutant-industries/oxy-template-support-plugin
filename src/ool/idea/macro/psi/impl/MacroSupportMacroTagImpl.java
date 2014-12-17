package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.macro.psi.MacroSupportBlockStatement;
import ool.idea.macro.psi.MacroSupportMacroAttribute;
import ool.idea.macro.psi.MacroSupportMacroTag;
import ool.idea.macro.psi.MacroSupportMacroUnpairedTag;
import ool.idea.macro.psi.MacroSupportMacroXmlPrefix;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportMacroTagImpl extends ASTWrapperPsiElement implements MacroSupportMacroTag
{

    public MacroSupportMacroTagImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroTag(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<MacroSupportBlockStatement> getBlockStatementList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportBlockStatement.class);
    }

    @Override
    @NotNull
    public List<MacroSupportMacroAttribute> getMacroAttributeList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportMacroAttribute.class);
    }

    @Override
    @NotNull
    public List<MacroSupportMacroTag> getMacroTagList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportMacroTag.class);
    }

    @Override
    @NotNull
    public List<MacroSupportMacroUnpairedTag> getMacroUnpairedTagList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportMacroUnpairedTag.class);
    }

    @Override
    @NotNull
    public List<MacroSupportMacroXmlPrefix> getMacroXmlPrefixList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportMacroXmlPrefix.class);
    }

}
