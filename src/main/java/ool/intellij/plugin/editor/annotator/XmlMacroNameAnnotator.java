package ool.intellij.plugin.editor.annotator;

import ool.intellij.plugin.editor.inspection.fix.NotMatchingTagsQuickFix;
import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.MacroTag;
import ool.intellij.plugin.psi.visitor.OxyTemplateAnnotatingVisitor;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroNameAnnotator extends OxyTemplateAnnotatingVisitor
{
    @Override
    public void visitMacroName(@NotNull MacroName macroName)
    {
        PsiReference reference;

        if ((reference = macroName.getReference()) == null || reference.resolve() == null)
        {
            holder.newAnnotation(HighlightSeverity.WARNING, I18nSupport.message("annotator.unresolved.macro.reference.tooltip")).create();
        }

        if (macroName.isClosingTagMacroName())
        {
            MacroTag tag;

            if ((tag = PsiTreeUtil.getParentOfType(macroName, MacroTag.class)) == null)
            {
                return;
            }

            if ( ! tag.getMacroName().getText().equals(macroName.getText()))
            {
                holder.newAnnotation(HighlightSeverity.WARNING, I18nSupport.message("annotator.not.matching.tags.reference.tooltip")).range(macroName)
                        .withFix(new NotMatchingTagsQuickFix(macroName, tag.getMacroName()))
                        .withFix(new NotMatchingTagsQuickFix(tag.getMacroName(), macroName)).create();
            }
        }
    }

}
