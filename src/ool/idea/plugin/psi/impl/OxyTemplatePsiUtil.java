package ool.idea.plugin.psi.impl;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroNameIdentifier;
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
    public static String getMacroNamespace(@NotNull MacroName macroName)
    {
        List<MacroNameIdentifier> macroNameIdentifierList = macroName.getMacroNameIdentifierList();

        if(macroNameIdentifierList.size() == 1)
        {
            return macroNameIdentifierList.get(0).getText();
        }

        StringBuilder macroNamespace = new StringBuilder(macroNameIdentifierList.get(0).getText());

        macroNameIdentifierList.remove(0);
        macroNameIdentifierList.remove(macroNameIdentifierList.size() - 1);

        for(MacroNameIdentifier macroNameIdentifier : macroNameIdentifierList)
        {
            macroNamespace.append("." + macroNameIdentifier.getText());
        }

        return macroNamespace.toString();
    }

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

        PsiElement reference = OxyTemplateIndexUtil.getMacroNameReference(partialText, macroNameIdentifier.getProject());

        if(reference != null)
        {
            if(reference instanceof JSElement)
            {
                return new JsMacroReference(macroNameIdentifier, (JSElement)reference);
            }
            else if(reference instanceof PsiIdentifier)
            {
                return new JavaMacroReference(macroNameIdentifier, (PsiIdentifier)reference);
            }
        }

        return null;
    }

}
