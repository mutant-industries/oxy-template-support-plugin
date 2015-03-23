package ool.idea.plugin.psi.visitor;

import ool.idea.plugin.psi.MacroEmptyTag;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DirectiveStatementVisitor extends OxyTemplateRecursiveElementVisitor
{
    @Override
    public void visitMacroEmptyTag(@NotNull MacroEmptyTag o)
    {

    }

}
