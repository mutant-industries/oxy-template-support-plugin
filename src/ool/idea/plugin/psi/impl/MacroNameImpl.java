package ool.idea.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.Collection;
import ool.idea.plugin.file.index.JavaMacroNameIndex;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateVisitor;
import ool.idea.plugin.psi.reference.JavaMacroReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacroNameImpl extends ASTWrapperPsiElement implements MacroName
{
    public MacroNameImpl(ASTNode node)
    {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor)
    {
        if (visitor instanceof OxyTemplateVisitor) ((OxyTemplateVisitor) visitor).visitMacroName(this);
        else super.accept(visitor);
    }

    @Nullable
    @Override
    public PsiReference getReference()
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(getProject());

        String text = getText();

        if(text == null  || text.trim().length() == 0)
        {
            return null;
        }

        Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JavaMacroNameIndex.INDEX_ID,
                text.replace("oxy.", ""), allScope);

        if( ! files.isEmpty())
        {
            VirtualFile file = (VirtualFile) files.toArray()[0];
            PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);

            if(psiFile != null)
            {
                PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);

                if(psiClass != null)
                {
                    return new JavaMacroReference(this, psiClass);
                }
            }
        }

        return null;
    }

}
