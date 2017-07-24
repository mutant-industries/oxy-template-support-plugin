package ool.intellij.plugin.editor.format.block;

import java.util.ArrayList;
import java.util.List;

import ool.intellij.plugin.editor.format.block.builder.OxyTemplateInjectedBlockBuilder;
import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.formatter.blocks.JSBlock;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import com.intellij.xml.template.formatter.IndentInheritingBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extends JSBlock just because of type check, but has nothing in common with it. It's actually fake AST block, where
 * {@link OxyTemplateForeignElementWrapper#textRange} is more important than node itself. It wraps outer language tokens
 * from html / js point of view (T_OUTER_TEMPLATE_ELEMENT, T_INNER_TEMPLATE_ELEMENT)
 *
 * 2/23/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateForeignElementWrapper extends JSBlock implements IndentInheritingBlock
{
    protected final AbstractXmlTemplateFormattingModelBuilder builder;

    protected final OxyTemplateInjectedBlockBuilder injectedBlockBuilder;

    protected final XmlFormattingPolicy xmlPolicy;

    protected final CodeStyleSettings settings;

    protected Indent indent;

    private TextRange textRange;

    private List<Block> children;

    public OxyTemplateForeignElementWrapper(@NotNull AbstractXmlTemplateFormattingModelBuilder builder, @NotNull ASTNode node, Wrap wrap,
                                            Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy policy, Indent indent)
    {
        this(builder, node, wrap, alignment, settings, policy, indent, node.getTextRange());
    }

    public OxyTemplateForeignElementWrapper(@NotNull AbstractXmlTemplateFormattingModelBuilder builder, @NotNull ASTNode node, Wrap wrap,
                                            Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy policy, Indent indent, TextRange range)
    {
        super(node, alignment, indent, wrap, settings);

        this.xmlPolicy = policy;
        this.settings = settings;
        this.builder = builder;
        this.textRange = range != null ? range : node.getTextRange();
        this.indent = indent;
        injectedBlockBuilder = new OxyTemplateInjectedBlockBuilder(builder, policy);
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks()
    {
        if (children != null)
        {
            return children;
        }

        return children = buildChildren();
    }

    @NotNull
    public List<Block> buildChildren()
    {
        final FileViewProvider provider = myNode.getPsi().getContainingFile().getViewProvider();
        PsiElement element = provider.findElementAt(textRange.getStartOffset(), OxyTemplate.INSTANCE);

        assert element != null;

        while (OxyTemplateHelper.checkRangeContainsParent(element, textRange, OxyTemplateTypes.T_TEMPLATE_HTML_CODE))
        {
            element = element.getParent();
        }

        ASTNode child = element.getNode();

        final Alignment textAlignment = Alignment.createAlignment();
        ArrayList<Block> result = new ArrayList<>();

        while (child != null)
        {
            if ( ! textRange.contains(child.getTextRange()))
            {
                if (child.getStartOffset() < getTextRange().getEndOffset())
                {
                    while (child.getFirstChildNode() != null
                            && ! textRange.contains((child = child.getFirstChildNode()).getTextRange())) ;
                }
                else
                {
                    break;
                }
            }

            if ( ! FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0)
            {
                child = processChild(result, child, null, chooseAlignment(child, textAlignment), indent);
            }

            child = nextNode(child);
        }

        return result;
    }

    @Nullable
    protected ASTNode nextNode(@Nullable final ASTNode node)
    {
        ASTNode child = node;

        if (child != null && child.getTreeNext() == null &&
                ! child.getTreeParent().getPsi().isEquivalentTo(getNode().getPsi()))
        {
            child = child.getTreeParent();
        }
        if (child != null)
        {
            child = child.getTreeNext();
        }

        return child;
    }

    @Nullable
    protected ASTNode processChild(@NotNull List<Block> result, @NotNull final ASTNode child, @Nullable final Wrap wrap,
                                   @Nullable final Alignment alignment, @Nullable final Indent indent)
    {
        if (child.getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE)
        {
            return injectedBlockBuilder.buildInjectedBlocks(result, child, indent,
                    TextRange.create(child.getStartOffset(), getContentRange().getEndOffset()));
        }
        else if (child.getElementType() == OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE)
        {
            return injectedBlockBuilder.buildInjectedJsBlocks(result, child, indent,
                    TextRange.create(child.getStartOffset(), getContentRange().getEndOffset()));
        }
        else
        {
            processSimpleChild(result, child, indent, wrap, alignment);

            return child;
        }
    }

    protected void processSimpleChild(@NotNull final List<Block> result, @NotNull final ASTNode child, @Nullable final Indent indent,
                                      @Nullable final Wrap wrap, @Nullable final Alignment alignment)
    {
        if (child.getPsi() instanceof MacroCall)
        {
            MacroTagBlock tagBlock = new MacroTagBlock(builder, child, wrap, alignment, settings, xmlPolicy, indent);

            result.add(tagBlock);
        }
        else if (child.getPsi() instanceof DirectiveStatement)
        {
            result.add(new DirectiveBlock(builder, child, wrap, alignment, settings, xmlPolicy, indent));
        }
        else
        {
            Indent localIndent = indent;
            ASTNode nextSibling = child;

            /**
             * Open block marker has no indent in the case when it is followed by } and
             *  - it is the last child of this block - ...<%}
             *  - it is followed by white space, that fills the range of this block and the white space doesn't contain
             *      line breaks - ...<% }
             * In other cases @param indent is used.
             *
             * <% if (foo) { %>
             *
             * <% } %>
             *
             * <% if (foo) { %>
             *     <%
             * } %>
             */
            if (OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(child.getElementType())
                    && (child.getTextRange().getEndOffset() == textRange.getEndOffset()
                        || (nextSibling = child.getTreeNext()) != null
                        && nextSibling.getTextRange().getEndOffset() == textRange.getEndOffset()
                        && nextSibling.getPsi() instanceof PsiWhiteSpace && ! nextSibling.textContains('\n'))
                    && (nextSibling = nextSibling.getTreeNext()) != null && nextSibling.getText().startsWith("}"))
            {
                localIndent = Indent.getNoneIndent();
            }

            result.add(new OxyTemplateBlock(builder, child, wrap, alignment, settings, xmlPolicy, localIndent));
        }
    }

    @NotNull
    protected Block createSyntheticBlock(@NotNull final ArrayList<Block> localResult, @Nullable final Indent childrenIndent)
    {
        return new OxyTemplateSyntheticBlock(localResult, Indent.getNoneIndent(), childrenIndent);
    }

    /**
     * @return Limit range that cannot be exceeded by child blocks
     */
    @NotNull
    protected TextRange getContentRange()
    {
        return textRange;
    }

    // -------------------------------------------------------------------------------------------

    @Nullable
    protected Alignment chooseAlignment(@NotNull final ASTNode child, @NotNull final Alignment textAlignment)
    {
        if (child.getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE && xmlPolicy.getShouldAlignText())
        {
            return textAlignment;
        }

        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2)
    {
        if (child2 instanceof DirectiveBlock)
        {
            return Spacing.createSpacing(0, 0, 1, false, 0);
        }
        if (child1 instanceof OxyTemplateBlock && child2 instanceof InnerJsBlockWrapper)
        {
            OxyTemplateBlock marker = (OxyTemplateBlock) child1;

            if (OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(marker.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 1);
            }
        }
        if (child2 instanceof OxyTemplateBlock && child1 instanceof InnerJsBlockWrapper)
        {
            OxyTemplateBlock marker = (OxyTemplateBlock) child2;

            if (OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(marker.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 1);
            }
        }

        return null;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newChildIndex)
    {
        return new ChildAttributes(indent, null);
    }

    @NotNull
    @Override
    public TextRange getTextRange()
    {
        return textRange;
    }

    public void setTextRange(@NotNull final TextRange textRange)
    {
        if (children != null)
        {
            throw new IllegalStateException("! cannot set range after children are built !");
        }

        this.textRange = textRange;
    }

    @Nullable
    @Override
    public Indent getIndent()
    {
        return Indent.getNoneIndent();
    }

    @Override
    public void setIndent(@Nullable final Indent indent)
    {
        this.indent = indent;
    }

}
