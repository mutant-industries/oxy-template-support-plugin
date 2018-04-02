package ool.intellij.plugin.editor.annotator;

import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.highlighting.JSHighlighter;
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
    public void visitElement(PsiElement element)
    {
        if (element.getNode().getElementType() == JSTokenTypes.EACH_KEYWORD)
        {
            myHolder.createAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY, element.getTextRange(), null)
                    .setTextAttributes(JSHighlighter.JS_KEYWORD);
        }

        super.visitElement(element);
    }

    @Override
    public void visitJSReferenceExpression(JSReferenceExpression node)
    {
        if (MacroIndex.isInMacroDefinition(node))
        {
            return;
        }

        if (node.getParent() instanceof JSCallExpression)
        {
            String referenceText = MacroIndex.normalizeMacroName(node.getText());

            if (MacroIndex.isInMacroNamespace(referenceText) && node.resolve() == null)
            {
                myHolder.createWarningAnnotation(node, I18nSupport.message("annotator.unresolved.macro.reference.tooltip"));

                return;
            }
        }

        super.visitJSReferenceExpression(node);
    }

}
