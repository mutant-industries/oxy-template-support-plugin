package ool.idea.plugin.editor.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.validation.JavaScriptAnnotatingVisitor;
import com.intellij.psi.PsiElement;
import ool.idea.plugin.lang.I18nSupport;
import org.jetbrains.annotations.NotNull;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnresolvedJsMacroReferenceAnnotator extends JavaScriptAnnotatingVisitor
{
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
    {
        if(element instanceof JSCallExpression
                && element.getFirstChild() instanceof JSReferenceExpression)
        {
            JSReferenceExpression referenceExpression = (JSReferenceExpression) element.getFirstChild();

            if(referenceExpression.getText().startsWith("oxy.") && referenceExpression.resolve() == null)
            {
                holder.createWarningAnnotation(referenceExpression, I18nSupport.message("annotator.unresolved.macro.reference.name"));

                return;
            }
        }

        super.annotate(element, holder);
    }

}
