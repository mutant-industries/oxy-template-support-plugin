package ool.idea.plugin.editor.format.block.innerJs;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.common.InjectedLanguageBlockWrapper;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 3/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsBlockWrapper implements Block
{
    private final InjectedLanguageBlockWrapper injectedBlock;

    private final Indent childIndent;

    private final Indent indent;

    /**
     * @param original   parent block that overlaps {@param range}
     * @param offset     offset in specified range
     * @param range      range in the document
     * @param indent     indent of this block (none by default)
     * @param rootIndent indent to be applied to a block that fully covers {@param range}
     * @see InjectedLanguageBlockWrapper
     */
    public InnerJsBlockWrapper(@NotNull final InnerJsBlock original, final int offset, @Nullable TextRange range,
                               @Nullable Indent indent, @Nullable Indent rootIndent)
    {
        injectedBlock = new InjectedLanguageBlockWrapper(original, offset, range, rootIndent);
        this.childIndent = rootIndent;
        this.indent = indent;

        original.setAllowedRange(range);
        original.setInheritedIndent(rootIndent);
    }

    /**
     * @param original   parent block that overlaps {@param range}
     * @param offset     offset in specified range
     * @param range      range in the document
     * @param rootIndent indent to be applied to a block that fully covers {@param range}
     * @see InjectedLanguageBlockWrapper
     */
    public InnerJsBlockWrapper(@NotNull final InnerJsBlock original, final int offset, @Nullable TextRange range,
                               @Nullable Indent rootIndent)
    {
        this(original, offset, range, Indent.getNoneIndent(), rootIndent);
    }

    @Nullable
    @Override
    public Indent getIndent()
    {
        return indent;
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks()
    {
        return injectedBlock.getSubBlocks();
    }

    @Nullable
    @Override
    public Alignment getAlignment()
    {
        return injectedBlock.getAlignment();
    }

    @Nullable
    @Override
    public Wrap getWrap()
    {
        return injectedBlock.getWrap();
    }

    @NotNull
    @Override
    public TextRange getTextRange()
    {
        return injectedBlock.getTextRange();
    }

    @Nullable
    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        return injectedBlock.getSpacing(child1, child2);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newChildIndex)
    {
        return new ChildAttributes(childIndent, injectedBlock.getChildAttributes(newChildIndex).getAlignment());
    }

    @Override
    public boolean isIncomplete()
    {
        return injectedBlock.isIncomplete();
    }

    @Override
    public boolean isLeaf()
    {
        return injectedBlock.isLeaf();
    }

}
