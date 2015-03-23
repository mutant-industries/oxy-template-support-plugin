package ool.idea.plugin.psi.visitor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.psi.OxyTemplateElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateRecursiveElementVisitor extends OxyTemplateElementVisitor
{
    @Override
    public void visitElement(@NotNull PsiElement element)
    {
        super.visitElement(element);

        element.acceptChildren(this);
    }

    @Override
    public void visitFile(@NotNull PsiFile file)
    {
        for(PsiFile psiFile : file.getViewProvider().getAllFiles())
        {
            if( ! (psiFile instanceof OxyTemplateFile))
            {
                continue;
            }

            psiFile.acceptChildren(this);
        }

        super.visitFile(file);
    }

}
