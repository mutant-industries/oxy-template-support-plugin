package ool.idea.plugin.editor.format;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateLanguageBlock;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateBlock extends TemplateLanguageBlock
{
    private Indent indent;

    public OxyTemplateBlock(AbstractTemplateLanguageFormattingModelBuilder modelBuilder, ASTNode astNode, Wrap wrap, Alignment alignment, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        super(modelBuilder, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);

        this.indent = indent;
    }

    @Nullable
    @Override
    protected Spacing getSpacing(TemplateLanguageBlock templateLanguageBlock)
    {
        return getSpacing(this, templateLanguageBlock);
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
            else if (firstBlock.getNode().getElementType() == OxyTemplateTypes.DIRECTIVE_STATEMENT
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.DIRECTIVE_STATEMENT)
            {
                return Spacing.createSpacing(0, 1, 1, true, 0);
            }
// -----------------------------TODO -----------------------------------------------------------------------------------
            else if (firstBlock.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT
                    && ! secondBlock.getNode().getText().startsWith(" "))
            {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
        }
//        else if (child1 instanceof OxyTemplateBlock)
//        {
//            OxyTemplateBlock firstBlock = (OxyTemplateBlock) child1;
//
//            if (OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(firstBlock.getNode().getElementType()))
//            {
//                return Spacing.createSpacing(1, 1, 0, true, 0);
//            }
//        }
//        else if (child2 instanceof OxyTemplateBlock)
//        {
//            OxyTemplateBlock secondBlock = (OxyTemplateBlock) child2;
//
//            if(OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(secondBlock.getNode().getElementType()))
//            {
//                return Spacing.createSpacing(1, 1, 0, true, 0);
//            }
//        }
// ---------------------------------------------------------------------------------------------------------------------
        return null;
    }

    @NotNull
    @Override
    protected Indent getChildIndent(@NotNull ASTNode astNode)
    {
        return indent != null ? indent : Indent.getNoneIndent();
    }

    @NotNull
    public ChildAttributes getChildAttributes(int index)
    {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

}
