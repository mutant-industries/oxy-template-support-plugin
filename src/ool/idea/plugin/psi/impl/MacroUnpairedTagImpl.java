package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroUnpairedTag;
import ool.idea.plugin.psi.MacroXmlPrefix;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacroUnpairedTagImpl extends ASTWrapperPsiElement implements MacroUnpairedTag
{
    public MacroUnpairedTagImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroUnpairedTag(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<MacroAttribute> getMacroAttributeList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroAttribute.class);
    }

    @Override
    @Nullable
    public MacroName getMacroName()
    {
        return findChildByClass(MacroName.class);
    }

    @Override
    @NotNull
    public MacroXmlPrefix getMacroXmlPrefix()
    {
        return findNotNullChildByClass(MacroXmlPrefix.class);
    }

}
