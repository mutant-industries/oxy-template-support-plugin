package ool.idea.macro.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import ool.idea.macro.psi.MacroSupportDirectiveParamFileReference;
import ool.idea.macro.psi.MacroSupportVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportDirectiveParamFileReferenceImpl extends ASTWrapperPsiElement implements MacroSupportDirectiveParamFileReference
{

    public MacroSupportDirectiveParamFileReferenceImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof MacroSupportVisitor)
            ((MacroSupportVisitor) visitor).visitDirectiveParamFileReference(this);
        else super.accept(visitor);
    }

    @NotNull
    public PsiReference[] getReferences()
    {
        FileReferenceSet fileReferenceSet = new FileReferenceSet(this);
//        {
//            @Override
//            public FileType[] getSuitableFileTypes()
//            {
//                return new FileType[]{MacroSupportFileType.INSTANCE};
//            }
//        };
        return fileReferenceSet.getAllReferences();
    }

}
