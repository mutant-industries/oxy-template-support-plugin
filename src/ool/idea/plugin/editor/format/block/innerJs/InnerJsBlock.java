package ool.idea.plugin.editor.format.block.innerJs;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.formatter.blocks.JSBlock;
import com.intellij.lang.javascript.formatter.blocks.alignment.ASTNodeBasedAlignmentFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.InjectedLanguageBlockWrapper;
import com.intellij.webcore.template.formatter.IndentInheritingBlock;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ool.idea.plugin.editor.format.block.OxyTemplateBlock;
import ool.idea.plugin.editor.format.block.OxyTemplateForeignElementWrapper;
import ool.idea.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Always wrapped by {@link InjectedLanguageBlockWrapper} which is always wrapped by {@link InnerJsBlockWrapper}
 *
 * 2/10/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsBlock extends JSBlock implements IndentInheritingBlock
{
    private TextRange allowedRange;

    private List<Block> children;

    private boolean useInheritedIndent;

    private Indent inheritedIndent;

    public InnerJsBlock(ASTNode child, Alignment childAlignment, Indent inheritedIndent, Wrap wrap, CodeStyleSettings topSettings,
                        ASTNodeBasedAlignmentFactory sharedAlignmentFactory, Language dialect)
    {
        super(child, childAlignment, inheritedIndent, wrap, topSettings, sharedAlignmentFactory, dialect);
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks()
    {
        if(children != null)
        {
            return children;
        }

        List<Block> subBlocks = super.getSubBlocks();
        List<Block> rearrangedBlocks = new ArrayList<Block>(10);
        OxyTemplateForeignElementWrapper firstForeignBlock = null;
        OxyTemplateForeignElementWrapper lastForeignBlock = null;
        List<Block> afterLastForeignBlock = new LinkedList<Block>();

        for(Block block : subBlocks)
        {
            /**
             * Foreign blocks are merged <b>only</b> within allowedRange. Let's have the file with following contents:
             *
             * <%
             * if (foo) {
             *
             * }
             * %>
             *
             * The file root block ({@link OxyTemplateForeignElementWrapper}) has three subBlocks:
             *  - {@link OxyTemplateBlock} (0,2)
             *  - {@link InnerJsBlockWrapper} (3,16)
             *  - {@link OxyTemplateBlock} (17,19)
             * So if we asked for subBlocks of js wrapper without this condition, subBlocks would be merged to root block
             * again, which would finally result in a very slow death by {@link OutOfMemoryError}.
             */
            if(allowedRange != null && ! allowedRange.contains(block.getTextRange()))
            {
                rearrangedBlocks.add(block);
            }
            else if(block instanceof OxyTemplateForeignElementWrapper)
            {
                afterLastForeignBlock.clear();
                lastForeignBlock = (OxyTemplateForeignElementWrapper) block;

                if(firstForeignBlock == null)
                {
                    firstForeignBlock = lastForeignBlock;
                    firstForeignBlock.setIndent(Indent.getIndent(Indent.Type.NORMAL, false, true));
                }
            }
            else if(firstForeignBlock == null)
            {
                rearrangedBlocks.add(block);
            }
            else
            {
                afterLastForeignBlock.add(block);
            }

            if(block instanceof InnerJsBlock)
            {
                ((InnerJsBlock) block).inheritedIndent = inheritedIndent;

                /**
                 * block covers the range passed to {@link InnerJsBlockWrapper}, therefore custom indent will be applied
                 */
                if(allowedRange != null && allowedRange.contains(block.getTextRange()))
                {
                    ((InnerJsBlock) block).setIndent(inheritedIndent);
                }
                else
                {
                    ((InnerJsBlock) block).allowedRange = allowedRange;
                }
            }
        }

        if(firstForeignBlock != null)
        {
            /**
             * merge blocks between including first and last foreign subBlock, e.g.:
             *
             * <% ...{ %>
             *      <m:foo param="expr: false" />
             * <% } %>
             * - subBlocks nodes: T_INNER_TEMPLATE_ELEMENT '<m:foo param="expr:', js ' false', T_INNER_TEMPLATE_ELEMENT '" />',
             * the second block is skipped and the first one's range is adjusted to cover the whole tag
             */
            firstForeignBlock.setTextRange(TextRange.create(firstForeignBlock.getTextRange().getStartOffset(),
                    lastForeignBlock.getTextRange().getEndOffset()));

            for(Block block : firstForeignBlock.getSubBlocks())
            {
                rearrangedBlocks.add(block);
            }

            for(Block block : afterLastForeignBlock)
            {
                rearrangedBlocks.add(block);
            }
        }

        return children = rearrangedBlocks;
    }

    // ------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        if(child1 instanceof OxyTemplateBlock && ! (child2 instanceof OxyTemplateBlock))
        {
            OxyTemplateBlock block = (OxyTemplateBlock) child1;

            if(OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(block.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 1);
            }
        }
        else if( ! (child1 instanceof OxyTemplateBlock) && child2 instanceof OxyTemplateBlock)
        {
            OxyTemplateBlock block = (OxyTemplateBlock) child2;

            if(OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(block.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 1);
            }
        }

        return super.getSpacing(child1, child2);
    }

    public void setAllowedRange(@Nullable TextRange allowedRange)
    {
        if(children != null)
        {
            throw new IllegalStateException("! cannot set range after children are built !");
        }

        this.allowedRange = allowedRange;
    }

    public void setInheritedIndent(@Nullable Indent indent)
    {
        this.inheritedIndent = indent;
    }

    @Nullable
    @Override
    public Indent getIndent()
    {
        return useInheritedIndent ? inheritedIndent : super.getIndent();
    }

    @Override
    public void setIndent(@Nullable Indent indent)
    {
        inheritedIndent = indent;
        useInheritedIndent = true;
    }

}
