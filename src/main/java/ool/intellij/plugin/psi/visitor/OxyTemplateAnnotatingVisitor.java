package ool.intellij.plugin.psi.visitor;

import ool.intellij.plugin.psi.OxyTemplateElementVisitor;
import ool.intellij.plugin.psi.OxyTemplatePsiElement;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 2/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class OxyTemplateAnnotatingVisitor extends OxyTemplateElementVisitor implements Annotator
{
    protected AnnotationHolder holder;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
    {
        if (element instanceof OxyTemplatePsiElement)
        {
            try
            {
                this.holder = holder;

                element.accept(this);
            }
            finally
            {
                this.holder = null;
            }
        }
    }

}
