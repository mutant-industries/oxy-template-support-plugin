package ool.idea.plugin.psi.visitor;

import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.MacroUnpairedTag;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DirectiveStatementVisitor extends OxyTemplateRecursiveElementVisitor
{
    @Override
    public void visitMacroTag(@NotNull MacroTag o)
    {

    }

    @Override
    public void visitMacroUnpairedTag(@NotNull MacroUnpairedTag o)
    {

    }

}
