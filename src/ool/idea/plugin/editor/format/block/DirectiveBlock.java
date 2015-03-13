package ool.idea.plugin.editor.format.block;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DirectiveBlock extends OxyTemplateBlock
{
    public DirectiveBlock(AbstractTemplateLanguageFormattingModelBuilder modelBuilder, @NotNull ASTNode astNode, @Nullable Wrap wrap,
                          @Nullable Alignment alignment, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        super(modelBuilder, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
    }

    @Nullable
    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        if (child1 instanceof OxyTemplateBlock && child2 instanceof OxyTemplateBlock)
        {
            OxyTemplateBlock firstBlock = (OxyTemplateBlock) child1;
            OxyTemplateBlock secondBlock = (OxyTemplateBlock) child2;

            if (firstBlock.getNode().getElementType() == OxyTemplateTypes.DIRECTIVE_OPEN_STATEMENT
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_DIRECTIVE)
            {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
            else if (firstBlock.getNode().getElementType() == OxyTemplateTypes.T_DIRECTIVE
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.DIRECTIVE_PARAM_WRAPPER)
            {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
            else if (firstBlock.getNode().getElementType() == OxyTemplateTypes.DIRECTIVE_PARAM_WRAPPER
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.BLOCK_CLOSE_STATEMENT)
            {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
        }

        return null;
    }

}
