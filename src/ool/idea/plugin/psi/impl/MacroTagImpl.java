package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.psi.BlockStatement;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.MacroUnpairedTag;
import ool.idea.plugin.psi.MacroXmlPrefix;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroTagImpl extends ASTWrapperPsiElement implements MacroTag
{
    public MacroTagImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroTag(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<BlockStatement> getBlockStatementList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, BlockStatement.class);
    }

    @Override
    @NotNull
    public List<MacroAttribute> getMacroAttributeList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroAttribute.class);
    }

    @Override
    @NotNull
    public List<MacroName> getMacroNameList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroName.class);
    }

    @Override
    @NotNull
    public List<MacroTag> getMacroTagList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroTag.class);
    }

    @Override
    @NotNull
    public List<MacroUnpairedTag> getMacroUnpairedTagList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroUnpairedTag.class);
    }

    @Override
    @NotNull
    public List<MacroXmlPrefix> getMacroXmlPrefixList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroXmlPrefix.class);
    }

}
