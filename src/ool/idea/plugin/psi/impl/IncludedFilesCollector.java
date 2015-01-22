package ool.idea.plugin.psi.impl;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import java.util.HashMap;
import java.util.Map;
import ool.idea.plugin.psi.DirectiveParamFileReference;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludedFilesCollector extends PsiRecursiveElementVisitor
{
    private final Map<DirectiveParamFileReference, PsiFile> includediles;
    private final Map<DirectiveParamFileReference, PsiFile> recursionGuard;

    public IncludedFilesCollector()
    {
        this.includediles = new HashMap<DirectiveParamFileReference, PsiFile>();
        this.recursionGuard = new HashMap<DirectiveParamFileReference, PsiFile>();
    }

    @Override
    public void visitElement(PsiElement element)
    {
        PsiFileSystemItem item;

        if ((element instanceof DirectiveParamFileReference))
        {
            for (PsiReference reference : element.getReferences())
            {
                if (reference instanceof FileReference && (item = ((FileReference) reference).resolve()) != null
                        && ! (item instanceof PsiDirectory) && item.getContainingFile() != null)
                {
                    if( ! includediles.containsValue(item.getContainingFile())
                            &&  ! recursionGuard.containsValue(item.getContainingFile()))
                    {
                        recursionGuard.put((DirectiveParamFileReference) element, item.getContainingFile());

                        item.getContainingFile().acceptChildren(this);
                    }

                    includediles.put((DirectiveParamFileReference) element, item.getContainingFile());
                }
            }
        }

        super.visitElement(element);
    }

    public Map<DirectiveParamFileReference, PsiFile> getResult()
    {
        return includediles;
    }

}
