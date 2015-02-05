package ool.idea.plugin.editor.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.MacroName;
import org.jetbrains.annotations.NotNull;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnresolvedXmlMacroReferenceAnnotator implements Annotator
{
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
    {
        MacroName macroName;

        if(element instanceof MacroName && ! (macroName = (MacroName)element).isClosingTagMacroName())
        {
            PsiReference reference;

            if((reference = macroName.getReference()) == null || reference.resolve() == null)
            {
                holder.createWarningAnnotation(macroName, I18nSupport.message("annotator.unresolved.macro.reference.name"));
            }
        }
    }

}
