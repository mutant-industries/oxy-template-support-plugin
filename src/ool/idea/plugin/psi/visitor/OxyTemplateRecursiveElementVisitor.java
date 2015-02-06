package ool.idea.plugin.psi.visitor;

import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.psi.OxyTemplateElementVisitor;
import ool.idea.plugin.psi.OxyTemplatePsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateRecursiveElementVisitor extends OxyTemplateElementVisitor
{
    @Override
    public void visitOxyTemplatePsiElement(@NotNull OxyTemplatePsiElement element)
    {
        super.visitOxyTemplatePsiElement(element);

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
