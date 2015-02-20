package ool.idea.plugin.editor.format.builder;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlText;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import java.util.List;
import ool.idea.plugin.editor.format.MacroTagBlock;
import ool.idea.plugin.editor.format.OxyTemplateBlock;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/17/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class HtmlInjectedBlockBuilder extends AbstractInjectedBlockBuilder
{
    public HtmlInjectedBlockBuilder(@NotNull final XmlFormattingPolicy formattingPolicy,
                                    @NotNull final AbstractTemplateLanguageFormattingModelBuilder builder)
    {
        super(builder, formattingPolicy);
    }

    @NotNull
    @Override
    protected Language getInjectedElementsLanguage()
    {
        return OxyTemplate.INSTANCE;
    }

    @NotNull
    @Override
    protected IElementType getOuterLanguageElement()
    {
        return OxyTemplateTypes.T_TEMPLATE_HTML_CODE;
    }

    @Nullable
    @Override
    protected ASTNode shiftOriginalNode(@NotNull final ASTNode originalNode, @NotNull final List<Block> result)
    {
        ASTNode current = originalNode;

        while(current != null && current.getStartOffset() + current.getTextLength()
                < result.get(result.size() - 1).getTextRange().getEndOffset())
        {
            if(current.getTreeNext() == null
                    && current.getPsi().getParent() instanceof XmlText)
            {
                current = current.getTreeParent();
            }

            current = current.getTreeNext();

            /**
             * the case when typically end macro tag is part of XmlText followed by text, e.g.:
             *
             *  <div>
             *      <m:oxy.ifTrue>
             *          <br/>
             *      </m:oxy.ifTrue>
             *      Pol, nobilis fiscina!<br/>
             *  </div>
             */
            if(current != null && current.getStartOffset() + current.getTextLength()
                    > result.get(result.size() - 1).getTextRange().getEndOffset())
            {
                current = current.getFirstChildNode();
            }
        }

        return current;
    }

    @NotNull
    @Override
    public Block createInjectedBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                                     @Nullable Indent indent, @NotNull TextRange range)
    {
        final Block resultBlock;

        if(node.getPsi() instanceof MacroCall)
        {
            resultBlock = new MacroTagBlock(builder, node, null, null, getSettings(),
                    policy, indent);
        }
        else
        {
            resultBlock = new OxyTemplateBlock(builder, node, null, null, getSettings(),
                    policy, indent);
        }

        return createInjectedBlock(node, resultBlock, indent, node.getStartOffset(), range);
    }

}
