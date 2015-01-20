package ool.idea.plugin.psi.impl;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.MacroNameIdentifier;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.reference.JavaMacroReference;
import ool.idea.plugin.psi.reference.JsMacroReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplatePsiUtil
{
    @NotNull
    public static PsiReference[] getReferences(@NotNull DirectiveParamFileReference directiveParamFileReference)
    {
        return new FileReferenceSet(directiveParamFileReference).getAllReferences();
    }

    @Nullable
    public static PsiReference getReference(@NotNull MacroNameIdentifier macroNameIdentifier)
    {
        String partialText = macroNameIdentifier.getParent().getText().substring(0,
                macroNameIdentifier.getStartOffsetInParent() + macroNameIdentifier.getTextLength());

        List<JSElement> jsMacroReferences;
        PsiIdentifier javaMacroReference;

        if((javaMacroReference = OxyTemplateIndexUtil.getJavaMacroNameReference(partialText,
                macroNameIdentifier.getProject())) != null)
        {
            return new JavaMacroReference(macroNameIdentifier, javaMacroReference);
        }
        else if((jsMacroReferences = OxyTemplateIndexUtil.getJsMacroNameReferences(partialText,
                macroNameIdentifier.getProject())).size() > 0)
        {
            return new JsMacroReference(macroNameIdentifier, jsMacroReferences
                    .toArray(new JSElement[jsMacroReferences.size()]));
        }

        return null;
    }

    public static PsiElement setName(MacroNameIdentifier macroNameIdentifier, String newName)
    {
        return macroNameIdentifier.replace(OxyTemplateElementFactory
                .createMacroNameIdentifier(macroNameIdentifier.getProject(), newName));
    }

    public static String getName(MacroNameIdentifier macroNameIdentifier)
    {
        return macroNameIdentifier.getText();
    }

}
