package ool.intellij.plugin.psi.impl;

import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.macro.param.MacroParamHelper;
import ool.intellij.plugin.psi.macro.param.MacroParamSuggestionSet;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroCallImpl extends OxyTemplatePsiElementImpl implements MacroCall
{
    public MacroCallImpl(@NotNull ASTNode node)
    {
        super(node);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + getMacroName().getName();
    }

    @Override
    public PsiElement setName(@NotNull String newName)
    {
        return getMacroName().setName(newName);
    }

    @NotNull
    @Override
    public MacroParamSuggestionSet getParamSuggestionSet()
    {
        PsiReference reference = getMacroName().getReference();
        PsiElement macroImplementation;

        if (reference != null && (macroImplementation = reference.resolve()) != null)
        {
            return MacroParamHelper.getMacroParamSuggestions(macroImplementation);
        }

        return MacroParamSuggestionSet.empty();
    }

}
