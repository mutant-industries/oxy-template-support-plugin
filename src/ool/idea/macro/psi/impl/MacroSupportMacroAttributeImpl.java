package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.macro.psi.MacroSupportMacroAttribute;
import ool.idea.macro.psi.MacroSupportMacroExpressionParameter;
import ool.idea.macro.psi.MacroSupportMacroParameter;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacroSupportMacroAttributeImpl extends ASTWrapperPsiElement implements MacroSupportMacroAttribute
{

    public MacroSupportMacroAttributeImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor) ((MacroSupportVisitor) visitor).visitMacroAttribute(this);
        else super.accept(visitor);
    }

    @Override
    @Nullable
    public MacroSupportMacroExpressionParameter getMacroExpressionParameter()
    {
        return findChildByClass(MacroSupportMacroExpressionParameter.class);
    }

    @Override
    @Nullable
    public MacroSupportMacroParameter getMacroParameter()
    {
        return findChildByClass(MacroSupportMacroParameter.class);
    }

}
