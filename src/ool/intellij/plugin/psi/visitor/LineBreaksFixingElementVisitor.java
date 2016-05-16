package ool.intellij.plugin.psi.visitor;

import java.util.LinkedList;

import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.RecursiveTreeElementVisitor;
import com.intellij.psi.impl.source.tree.TreeElement;
import org.jetbrains.annotations.NotNull;

/**
 * Just before javascript is passed to lexer, js tokens have to be concatenated somehow, which is done in {@link ool.intellij.plugin.psi.OxyTemplateInnerJsElementType#appendCurrentTemplateToken}.
 * The added line breaks result in white space tokens, that have to be removed, when the parsing is done, which happens in {@link #visitLeaf(LeafElement)}
 *
 * 5/15/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class LineBreaksFixingElementVisitor extends RecursiveTreeElementVisitor
{
    private final LinkedList<Integer> offsets;
    private int shift;

    public LineBreaksFixingElementVisitor(@NotNull LinkedList<Integer> offsets)
    {
        this.offsets = offsets;
        this.shift = 0;
    }

    @Override
    protected boolean visitNode(TreeElement element)
    {
        return true;
    }

    @Override
    public void visitLeaf(LeafElement leaf)
    {
        if (offsets.isEmpty() || (shift + leaf.getTextOffset() + leaf.getTextLength() < offsets.peekFirst()))
        {
            return;
        }

        while ( ! offsets.isEmpty() && offsets.peekFirst() < shift + leaf.getTextOffset())
        {
            offsets.pollFirst();
        }

        StringBuilder newText = new StringBuilder(leaf.getText());
        int localShift = 0;

        while ( ! offsets.isEmpty() && offsets.peekFirst() < shift + leaf.getTextOffset() + leaf.getTextLength())
        {
            int index = offsets.pollFirst() - (shift + localShift + leaf.getTextOffset());
            newText.deleteCharAt(index);
            localShift++;
        }

        shift += localShift;

        if (newText.length() > 0)
        {
            TreeElement newAnchor = Factory.createSingleLeafElement(leaf.getElementType(), newText,
                    0, newText.length(), null, leaf.getManager());

            leaf.rawInsertBeforeMe(newAnchor);
        }

        leaf.rawRemove();
    }

}
