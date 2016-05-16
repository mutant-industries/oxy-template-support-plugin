package ool.intellij.plugin.editor.annotator;

import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang3.StringEscapeUtils;
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

        if (StringEscapeUtils.unescapeHtml4(element.getText()).equals(element.getText()))
        {
            holder.createWarningAnnotation(TextRange.create(element.getTextRange().getStartOffset() + 1,
                    element.getTextRange().getEndOffset() - 1), I18nSupport.message("annotator.invalid.entity.tooltip"));
        }
    }

}
