package ool.idea.plugin.editor.format.block;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
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
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateLanguageBlock;
import java.util.List;
import ool.idea.plugin.editor.format.block.innerJs.InnerJsBlock;
import ool.idea.plugin.editor.format.block.innerJs.InnerJsBlockWrapper;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.lang.parser.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.DirectiveParamWrapper;
import ool.idea.plugin.psi.MacroXmlPrefix;
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
    public OxyTemplateBlock(AbstractTemplateLanguageFormattingModelBuilder modelBuilder,
                            @NotNull ASTNode astNode, @Nullable Wrap wrap, @Nullable Alignment alignment, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        super(modelBuilder, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
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
        if (child1 instanceof OxyTemplateBlock && child2 instanceof InnerJsBlockWrapper)
        {
            OxyTemplateBlock firstBlock = (OxyTemplateBlock) child1;

            if (firstBlock.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT)
            {
                return Spacing.createSpacing(1, 1, 0, true, 0);
            }
        }

        return null;
    }

    @Override
    protected void addBlocksForNonMarkupChild(List<Block> list, ASTNode astNode)
    {
        // param="expr: ..."
        if (astNode.getElementType() == OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE)
        {
            PsiElement childPsi = astNode.getPsi();
            FormattingModelBuilder modelBuilder = LanguageFormatting.INSTANCE.forContext(OxyTemplateInnerJs.INSTANCE, childPsi);
            PsiFile jsPsiFile = childPsi.getContainingFile().getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE);

            assert modelBuilder != null && jsPsiFile != null;

            FormattingModel childModel = modelBuilder.createModel(jsPsiFile, getSettings());
            list.add(new InnerJsBlockWrapper((InnerJsBlock)childModel.getRootBlock(), astNode.getStartOffset(),
                    astNode.getTextRange(), Indent.getSpaceIndent(2, true), null));
        }
        else
        {
            super.addBlocksForNonMarkupChild(list, astNode);
        }
    }

    @NotNull
    @Override
    protected Indent getChildIndent(@NotNull ASTNode astNode)
    {
        return Indent.getNoneIndent();
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int index)
    {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public boolean isLeaf()
    {
        return OxyTemplateParserDefinition.COMMENTS.contains(getNode().getElementType())
                || OxyTemplateParserDefinition.CLOSE_BLOCK_MARKERS.contains(getNode().getElementType())
                || OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS.contains(getNode().getElementType())
                || getNode().getPsi() instanceof DirectiveParamWrapper || getNode().getPsi() instanceof MacroXmlPrefix;
    }

}
