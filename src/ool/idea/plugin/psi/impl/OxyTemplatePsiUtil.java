package ool.idea.plugin.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroEmptyTag;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.reference.MacroReferenceSet;
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

    @NotNull
    public static PsiReference[] getReferences(@NotNull MacroName macroName)
    {
        return new MacroReferenceSet(macroName).getAllReferences();
    }

    @Nullable
    public static PsiReference getReference(@NotNull MacroName macroName)
    {
        PsiReference[] references = macroName.getReferences();

        return references.length > 0 ? references[0] : null;
    }

    public static PsiElement setName(@NotNull MacroName macroName, @NotNull String newName)
    {
        return macroName.replace(OxyTemplateElementFactory.createMacroName(macroName.getProject(), newName));
    }

    @NotNull
    public static String getName(@NotNull MacroName macroName)
    {
        return macroName.getText();
    }

    public static boolean isClosingTagMacroName(@NotNull final MacroName macroName)
    {
        if(PsiTreeUtil.getParentOfType(macroName, MacroEmptyTag.class) != null)
        {
            return false;
        }

        MacroCall tag = PsiTreeUtil.getParentOfType(macroName, MacroCall.class);
        MacroName openingTagMacroName;

        return tag != null && (openingTagMacroName = tag.getMacroName()) != null &&
                ! openingTagMacroName.isEquivalentTo(macroName);
    }

    public static String getName(@NotNull DirectiveStatement directiveStatement)
    {
        PsiElement psiElement = directiveStatement.getDirectiveOpenStatement().getNextSibling();

        if(psiElement instanceof PsiWhiteSpace)
        {
            psiElement = psiElement.getNextSibling();
        }

        return psiElement.getText();
    }

}
