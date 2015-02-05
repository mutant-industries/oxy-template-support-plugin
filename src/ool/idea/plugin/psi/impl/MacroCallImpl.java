package ool.idea.plugin.psi.impl;

import com.intellij.lang.ASTNode;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroName;
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
        MacroName macroName = getMacroName();

        return super.toString() + (macroName == null ? "" : " " + macroName.getName());
    }

}
