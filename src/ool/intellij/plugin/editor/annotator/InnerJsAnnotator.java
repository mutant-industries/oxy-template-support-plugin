package ool.intellij.plugin.editor.annotator;

import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.validation.JavaScriptAnnotatingVisitor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsAnnotator extends JavaScriptAnnotatingVisitor
{
    public InnerJsAnnotator(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder)
    {
        super(psiElement, holder);
    }

    @Override
    protected void checkCallReferences(JSCallExpression node, JSReferenceExpression referenceExpression)
    {
        String referenceText = MacroIndex.normalizeMacroName(referenceExpression.getText());

        if (MacroIndex.isInMacroNamespace(referenceText) && referenceExpression.resolve() == null)
        {
            myHolder.createWarningAnnotation(referenceExpression, I18nSupport.message("annotator.unresolved.macro.reference.tooltip"));

            return;
        }

        super.checkCallReferences(node, referenceExpression);
    }

    @Override
    public void visitJSReferenceExpression(JSReferenceExpression node)
    {
        if (MacroIndex.isInMacroDefinition(node))
        {
            return;
        }

        super.visitJSReferenceExpression(node);
    }

}
