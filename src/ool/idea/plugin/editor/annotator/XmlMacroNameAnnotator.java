package ool.idea.plugin.editor.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.editor.inspection.fix.NotMatchingTagsQuickFix;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.visitor.OxyTemplateAnnotatingVisitor;
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

        if((reference = macroName.getReference()) == null || reference.resolve() == null)
        {
            holder.createWarningAnnotation(macroName, I18nSupport.message("annotator.unresolved.macro.reference.tooltip"));
        }

        if(macroName.isClosingTagMacroName())
        {
            MacroTag tag;

            if((tag = PsiTreeUtil.getParentOfType(macroName, MacroTag.class)) == null)
            {
                return;
            }

            if( ! tag.getMacroName().getText().equals(macroName.getText()))
            {
                Annotation annotation = holder.createWarningAnnotation(macroName,
                        I18nSupport.message("annotator.not.matching.tags.reference.tooltip"));

                annotation.registerFix(new NotMatchingTagsQuickFix(macroName, tag.getMacroName()));
                annotation.registerFix(new NotMatchingTagsQuickFix(tag.getMacroName(), macroName));
            }
        }
    }

}
