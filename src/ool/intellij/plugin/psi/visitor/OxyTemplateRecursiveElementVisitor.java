package ool.intellij.plugin.psi.visitor;

import ool.intellij.plugin.file.OxyTemplateFile;
import ool.intellij.plugin.psi.OxyTemplateElementVisitor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
        file.getViewProvider().getAllFiles().stream()
                .filter(psiFile -> (psiFile instanceof OxyTemplateFile))
                .forEach(psiFile -> psiFile.acceptChildren(this));

        super.visitFile(file);
    }

}
