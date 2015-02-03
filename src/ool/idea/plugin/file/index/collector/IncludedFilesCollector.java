package ool.idea.plugin.file.index.collector;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.psi.DirectiveParamFileReference;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludedFilesCollector extends PsiRecursiveElementVisitor
{
    private final Map<DirectiveParamFileReference, VirtualFile> includediles;
    private final List<VirtualFile> recursionGuard;

    public IncludedFilesCollector()
    {
        this.includediles = new HashMap<DirectiveParamFileReference, VirtualFile>();
        this.recursionGuard = new LinkedList<VirtualFile>();
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
                    VirtualFile file = item.getContainingFile().getVirtualFile();

                    if( ! includediles.containsValue(file) &&  ! recursionGuard.contains(file))
                    {
                        recursionGuard.add(file);

                        item.getContainingFile().acceptChildren(this);
                    }

                    includediles.put((DirectiveParamFileReference) element, file);
                }
            }
        }

        super.visitElement(element);
    }

    public Map<DirectiveParamFileReference, VirtualFile> getResult()
    {
        return includediles;
    }

}
