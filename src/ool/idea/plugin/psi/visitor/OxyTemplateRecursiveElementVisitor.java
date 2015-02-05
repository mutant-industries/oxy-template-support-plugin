package ool.idea.plugin.psi.visitor;

import com.intellij.psi.PsiFile;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.psi.OxyTemplateElement;
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
    public void visitOxyTemplateElement(@NotNull OxyTemplateElement element)
    {
        super.visitOxyTemplateElement(element);

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
