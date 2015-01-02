package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroExpressionParam;
import ool.idea.plugin.psi.MacroParam;
import ool.idea.plugin.psi.MacroParamName;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacroAttributeImpl extends ASTWrapperPsiElement implements MacroAttribute
{

    public MacroAttributeImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroAttribute(this);
        else super.accept(visitor);
    }

    @Override
    @Nullable
    public MacroExpressionParam getMacroExpressionParam()
    {
        return findChildByClass(MacroExpressionParam.class);
    }

    @Override
    @Nullable
    public MacroParam getMacroParam()
    {
        return findChildByClass(MacroParam.class);
    }

    @Override
    @NotNull
    public MacroParamName getMacroParamName()
    {
        return findNotNullChildByClass(MacroParamName.class);
    }

}
