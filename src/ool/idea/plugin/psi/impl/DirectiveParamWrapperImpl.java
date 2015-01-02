package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.DirectiveParamWrapper;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectiveParamWrapperImpl extends ASTWrapperPsiElement implements DirectiveParamWrapper
{
    public DirectiveParamWrapperImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitDirectiveParamWrapper(this);
        else super.accept(visitor);
    }

    @Override
    @Nullable
    public DirectiveParamFileReference getDirectiveParamFileReference()
    {
        return findChildByClass(DirectiveParamFileReference.class);
    }

}
