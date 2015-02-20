package ool.idea.plugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import ool.idea.plugin.psi.MacroCall;
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

    public PsiElement setName(String newName)
    {
        return getMacroName().setName(newName);
    }

}
