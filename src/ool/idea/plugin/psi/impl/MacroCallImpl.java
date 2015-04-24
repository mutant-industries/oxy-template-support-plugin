package ool.idea.plugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.macro.param.MacroParamHelper;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
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
