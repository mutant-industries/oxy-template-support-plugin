package ool.idea.plugin.editor.format.block.xml;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.TemplateSyntheticBlock;
import java.util.List;
import ool.idea.plugin.editor.format.block.OxyTemplateBlock;
import ool.idea.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * 3/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateXmlSyntheticBlock extends TemplateSyntheticBlock
{
    public OxyTemplateXmlSyntheticBlock(List<Block> list, Block block, Indent indent, XmlFormattingPolicy xmlFormattingPolicy, Indent indent1)
    {
        super(list, block, indent, xmlFormattingPolicy, indent1);
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        if(child1 instanceof OxyTemplateBlock && child2 instanceof InnerJsBlockWrapper
                || child2 instanceof OxyTemplateBlock && child1 instanceof InnerJsBlockWrapper)
        {
            return Spacing.createSpacing(1, 1, 0, true, 1);
        }

        return super.getSpacing(child1, child2);
    }

}
