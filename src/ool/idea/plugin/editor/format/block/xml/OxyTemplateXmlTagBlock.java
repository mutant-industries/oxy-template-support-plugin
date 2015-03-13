package ool.idea.plugin.editor.format.block.xml;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateXmlTagBlock;
import java.util.List;
import ool.idea.plugin.editor.format.block.builder.XmlInjectedBlockBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * 2/17/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateXmlTagBlock extends TemplateXmlTagBlock
{
    private final XmlInjectedBlockBuilder injectedBlockBuilder;

    public OxyTemplateXmlTagBlock(AbstractTemplateLanguageFormattingModelBuilder abstractTemplateLanguageFormattingModelBuilder,
                                  ASTNode astNode, Wrap wrap, Alignment alignment, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        super(abstractTemplateLanguageFormattingModelBuilder, astNode, wrap, alignment, xmlFormattingPolicy, indent);

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

}
