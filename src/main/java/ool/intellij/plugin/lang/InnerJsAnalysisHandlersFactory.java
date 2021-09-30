package ool.intellij.plugin.lang;

import ool.intellij.plugin.editor.annotator.InnerJsAnnotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.javascript.JSAnalysisHandlersFactory;
import com.intellij.lang.javascript.validation.JSAnnotatingVisitor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 5/11/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsAnalysisHandlersFactory extends JSAnalysisHandlersFactory
{
    @NotNull
    @Override
    public JSAnnotatingVisitor createAnnotatingVisitor(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder)
    {
        return new InnerJsAnnotator(psiElement, holder);
    }

}
