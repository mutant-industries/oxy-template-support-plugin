package ool.intellij.plugin.editor.format.block.builder;

import ool.intellij.plugin.editor.format.block.OxyTemplateForeignElementWrapper;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.formatter.xml.XmlInjectedLanguageBlockBuilder;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * 2/17/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlInjectedBlockBuilder extends XmlInjectedLanguageBlockBuilder
{
    private final AbstractTemplateLanguageFormattingModelBuilder builder;

    private final XmlFormattingPolicy policy;

    public XmlInjectedBlockBuilder(@NotNull final XmlFormattingPolicy formattingPolicy,
                                   @NotNull final AbstractTemplateLanguageFormattingModelBuilder builder)
    {
        super(formattingPolicy);
        this.builder = builder;
        this.policy = formattingPolicy;
    }

    @Override
    public Block createInjectedBlock(ASTNode node, Block originalBlock, Indent indent, int offset, TextRange range, Language language)
    {
        return new OxyTemplateForeignElementWrapper(builder, node, null, null, getSettings(), policy, indent, range);
    }

}
