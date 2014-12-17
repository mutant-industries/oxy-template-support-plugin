package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportMacroXmlPrefix;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportMacroXmlPrefixImpl extends ASTWrapperPsiElement implements MacroSupportMacroXmlPrefix
{

    public MacroSupportMacroXmlPrefixImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroXmlPrefix(this);
        else super.accept(visitor);
    }

}
