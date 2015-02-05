package ool.idea.plugin.editor.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.editor.annotator.fix.NotMatchingTagsQuickFix;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class NotMatchingTagsAnnotator implements Annotator
{
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
    {
        MacroName macroName;

        if(element instanceof MacroName && (macroName = (MacroName)element).isClosingTagMacroName())
        {
            MacroTag tag = PsiTreeUtil.getParentOfType(macroName, MacroTag.class);

            if(tag == null) // redundant np check
            {
                return;
            }

            if( ! tag.getMacroName().getText().equals(macroName.getText()))
            {
                Annotation annotation = holder.createWarningAnnotation(macroName,
                        I18nSupport.message("annotator.not.matching.tags.reference.name"));

                annotation.registerFix(new NotMatchingTagsQuickFix(macroName, tag.getMacroName()));
                annotation.registerFix(new NotMatchingTagsQuickFix(tag.getMacroName(), macroName));
            }
        }
    }

}
