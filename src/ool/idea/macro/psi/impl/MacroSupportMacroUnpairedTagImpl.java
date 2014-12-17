package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.macro.psi.MacroSupportMacroAttribute;
import ool.idea.macro.psi.MacroSupportMacroUnpairedTag;
import ool.idea.macro.psi.MacroSupportMacroXmlPrefix;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportMacroUnpairedTagImpl extends ASTWrapperPsiElement implements MacroSupportMacroUnpairedTag
{

    public MacroSupportMacroUnpairedTagImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroUnpairedTag(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<MacroSupportMacroAttribute> getMacroAttributeList()
    {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroSupportMacroAttribute.class);
    }

    @Override
    @NotNull
    public MacroSupportMacroXmlPrefix getMacroXmlPrefix()
    {
        return findNotNullChildByClass(MacroSupportMacroXmlPrefix.class);
    }

}
