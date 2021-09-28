package ool.intellij.plugin.lang.parser.tree;

import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.templateLanguages.SimpleTreePatcher;
import org.jetbrains.annotations.NotNull;

/**
 * 3/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsTreePatcher extends SimpleTreePatcher
{
    @Override
    public void insert(@NotNull CompositeElement parent, TreeElement anchorBefore, @NotNull OuterLanguageElement toInsert)
    {
        if ( ! OxyTemplateHelper.insertOuterElementToAST(parent, anchorBefore, toInsert))
        {
            super.insert(parent, anchorBefore, toInsert);
        }
    }

}
