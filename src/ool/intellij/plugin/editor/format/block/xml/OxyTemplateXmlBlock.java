package ool.intellij.plugin.editor.format.block.xml;

import java.util.List;

import ool.intellij.plugin.editor.format.block.OxyTemplateBlock;
import ool.intellij.plugin.editor.format.block.builder.XmlInjectedBlockBuilder;
import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateXmlBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 3/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateXmlBlock extends TemplateXmlBlock
{
    private final XmlInjectedBlockBuilder injectedBlockBuilder;

    public OxyTemplateXmlBlock(AbstractTemplateLanguageFormattingModelBuilder abstractTemplateLanguageFormattingModelBuilder,
                               ASTNode astNode, Wrap wrap, Alignment alignment, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, TextRange textRange)
    {
        super(abstractTemplateLanguageFormattingModelBuilder, astNode, wrap, alignment, xmlFormattingPolicy, indent, textRange);

        injectedBlockBuilder = new XmlInjectedBlockBuilder(xmlFormattingPolicy, abstractTemplateLanguageFormattingModelBuilder);
    }

    @Nullable
    @Override
    protected ASTNode processChild(List<Block> result, final ASTNode child, final Wrap wrap, final Alignment alignment,
                                   final Indent indent)
    {
        if (child.getPsi() instanceof OuterLanguageElement)
        {
            injectedBlockBuilder.addInjectedLanguageBlockWrapper(result, child, indent, 0, null);

            return child;
        }

        return super.processChild(result, child, wrap, alignment, indent);
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        if (child1 instanceof OxyTemplateBlock && child2 instanceof InnerJsBlockWrapper
                || child2 instanceof OxyTemplateBlock && child1 instanceof InnerJsBlockWrapper)
        {
            return Spacing.createSpacing(1, 1, 0, true, 1);
        }

        return super.getSpacing(child1, child2);
    }

}
