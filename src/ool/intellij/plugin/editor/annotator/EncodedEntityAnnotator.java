package ool.intellij.plugin.editor.annotator;

import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 3/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class EncodedEntityAnnotator implements Annotator
{
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
    {
        if (element.getNode().getElementType() != OxyTemplateTypes.T_XML_ENCODED_ENTITY)
        {
            return;
        }

        if (StringEscapeUtils.unescapeHtml(element.getText()).equals(element.getText()))
        {
            holder.newAnnotation(HighlightSeverity.WARNING, I18nSupport.message("annotator.invalid.entity.tooltip")).range(element).create();
        }
    }

}
