package ool.idea.plugin.editor.format;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageFormatting;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.InjectedLanguageBlockWrapper;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.html.HtmlTag;
import com.intellij.webcore.template.formatter.TemplateLanguageBlock;
import java.util.List;
import ool.idea.plugin.lang.parser.OxyTemplateParserDefinition;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.psi.BlockStatement;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.MacroUnpairedTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
class OxyTemplateLanguageBlock extends TemplateLanguageBlock
{
    public OxyTemplateLanguageBlock(OxyTemplateFormatter oxyTemplateFormatter, ASTNode astNode, Wrap wrap, Alignment alignment, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        super(oxyTemplateFormatter, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
    }

    @Nullable
    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2)
    {
        if (child1 instanceof OxyTemplateLanguageBlock
                && child2 instanceof OxyTemplateLanguageBlock)
        {
            OxyTemplateLanguageBlock firstBlock = (OxyTemplateLanguageBlock) child1;
            OxyTemplateLanguageBlock secondBlock = (OxyTemplateLanguageBlock) child2;

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
            else if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME || firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE)
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE)
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
            else if ((firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_NAME
                    && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_UNPAIRED_TAG_END) ||
                    (firstBlock.getNode().getElementType() == OxyTemplateTypes.MACRO_ATTRIBUTE
                            && secondBlock.getNode().getElementType() == OxyTemplateTypes.T_XML_UNPAIRED_TAG_END))
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
        }
        else if (child1 instanceof OxyTemplateLanguageBlock)
        {
            OxyTemplateLanguageBlock firstBlock = (OxyTemplateLanguageBlock) child1;

            if (firstBlock.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT)
            {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
            else if (OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(firstBlock.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
        }
        else if (child2 instanceof OxyTemplateLanguageBlock)
        {
            OxyTemplateLanguageBlock secondBlock = (OxyTemplateLanguageBlock) child2;

            if(OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(secondBlock.getNode().getElementType()))
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
        }

        return null;
    }

    @NotNull
    @Override
    protected Indent getChildIndent(@NotNull ASTNode astNode)
    {
        if (astNode.getPsi() instanceof MacroAttribute
                || astNode.getElementType() == OxyTemplateTypes.T_XML_UNPAIRED_TAG_END)
        {
            return Indent.getNormalIndent();
        }
        else if(getNode().getPsi() instanceof MacroTag &&
                (astNode instanceof HtmlTag || astNode.getPsi() instanceof BlockStatement
                        || astNode.getPsi() instanceof MacroUnpairedTag || astNode.getPsi() instanceof MacroTag))
        {
            return Indent.getNormalIndent();
        }

        return Indent.getNoneIndent();
    }

    @Override
    protected Spacing getSpacing(TemplateLanguageBlock templateLanguageBlock)
    {
        return getSpacing(this, templateLanguageBlock);
    }

    @Override
    protected void addBlocksForNonMarkupChild(List<Block> result, ASTNode child)
    {
        if (child.getElementType() == OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE)
        {
            PsiElement childPsi = child.getPsi();
            FormattingModelBuilder modelBuilder = LanguageFormatting.INSTANCE.forContext(OxyTemplateInnerJs.INSTANCE, childPsi);
            PsiFile jsPsiFile = childPsi.getContainingFile().getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE);

            if ((modelBuilder != null) && (jsPsiFile != null))
            {
                FormattingModel childModel = modelBuilder.createModel(jsPsiFile, getSettings());
                result.add(new InjectedLanguageBlockWrapper(childModel.getRootBlock(), child.getStartOffset(), child.getTextRange(), Indent.getNoneIndent()));
            }
        }
        else
        {
            super.addBlocksForNonMarkupChild(result, child);
        }
    }

}
