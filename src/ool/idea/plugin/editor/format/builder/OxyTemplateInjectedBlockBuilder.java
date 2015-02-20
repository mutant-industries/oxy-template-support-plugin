package ool.idea.plugin.editor.format.builder;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateXmlBlock;
import java.util.List;
import ool.idea.plugin.editor.format.OxyTemplateXmlTagBlock;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInjectedBlockBuilder extends AbstractInjectedBlockBuilder
{
    public OxyTemplateInjectedBlockBuilder(@NotNull final XmlFormattingPolicy formattingPolicy,
                                           @NotNull final AbstractTemplateLanguageFormattingModelBuilder builder)
    {
        super(builder, formattingPolicy);
    }

    @NotNull
    @Override
    protected Language getInjectedElementsLanguage()
    {
        return HTMLLanguage.INSTANCE;
    }

    @NotNull
    @Override
    protected IElementType getOuterLanguageElement()
    {
        return OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT;
    }

    @Override
    @Nullable
    protected ASTNode shiftOriginalNode(@NotNull ASTNode originalNode, @NotNull List<Block> result)
    {
        ASTNode current = originalNode;

        // shift current child to last node
        while(current != null)
        {
            /**
             * case when there is unclosed macro tag in the document and its contents covers the rest of file, e.g.:
             *
             *  <m:foo>
             *      <div>
             *          <m:bar>
             *      </div>
             *  </m:foo>
             */
            if(current.getTreeNext() != null && current.getTreeNext().getElementType() == TokenType.ERROR_ELEMENT
                    && current.getFirstChildNode() != null)
            {
                current = current.getFirstChildNode();
            }
            if (current.getStartOffset() + current.getTextLength()
                    >= result.get(result.size() - 1).getTextRange().getEndOffset())
            {
                break;  // next element is out of range of covered blocks
            }

            current = current.getTreeNext();
        }

        return current;
    }

    @NotNull
    @Override
    public Block createInjectedBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                                     @Nullable Indent indent, @NotNull TextRange range)
    {
        final Block resultBlock;

        if(node.getPsi() instanceof XmlTag)
        {
            resultBlock = new OxyTemplateXmlTagBlock(builder, node, wrap, alignment,
                    policy, indent);
        }
        else
        {
            resultBlock = new TemplateXmlBlock(builder, node, wrap, alignment,
                    policy, indent, range);
        }

        return createInjectedBlock(node, resultBlock, indent, node.getStartOffset(), range);
    }

}
