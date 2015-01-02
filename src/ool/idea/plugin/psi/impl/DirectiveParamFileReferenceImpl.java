package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import org.jetbrains.annotations.NotNull;

public class DirectiveParamFileReferenceImpl extends ASTWrapperPsiElement implements DirectiveParamFileReference
{
    public DirectiveParamFileReferenceImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitDirectiveParamFileReference(this);
        else super.accept(visitor);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences()
    {
        return new FileReferenceSet(this).getAllReferences();
    }

}
