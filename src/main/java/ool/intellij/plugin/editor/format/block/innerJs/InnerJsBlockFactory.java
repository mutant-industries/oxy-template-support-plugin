package ool.intellij.plugin.editor.format.block.innerJs;

import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingMode;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.formatter.JSBlockContext;
import com.intellij.lang.javascript.formatter.JSCodeStyleSettings;
import com.intellij.lang.javascript.formatter.blocks.JSBlock;
import com.intellij.lang.javascript.formatter.blocks.alignment.ASTNodeBasedAlignmentFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormattingDocumentModelImpl;
import com.intellij.psi.formatter.xml.HtmlPolicy;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/22/17
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsBlockFactory extends JSBlockContext
{
    private final AbstractXmlTemplateFormattingModelBuilder builder;

    public InnerJsBlockFactory(AbstractXmlTemplateFormattingModelBuilder builder, @NotNull CodeStyleSettings topSettings, @NotNull Language dialect, @Nullable JSCodeStyleSettings explicitSettings, @NotNull FormattingMode formattingMode)
    {
        super(topSettings, dialect, explicitSettings, formattingMode);

        this.builder = builder;
    }

    @NotNull
    @Override
    protected JSBlock createSubBlock(@NotNull ASTNode child, Alignment childAlignment, Indent childIndent, Wrap wrap, ASTNodeBasedAlignmentFactory sharedAlignmentFactory, @Nullable JSBlock parentBlock)
    {
        return new InnerJsBlock(child, childAlignment, childIndent, wrap, sharedAlignmentFactory, this);
    }

    @NotNull
    @Override
    public Block createBlock(@NotNull ASTNode child, @Nullable Wrap wrap, @Nullable Alignment childAlignment, @Nullable Indent childIndent, @Nullable ASTNodeBasedAlignmentFactory alignmentFactory, @Nullable JSBlock parentBlock)
    {
        if (child.getElementType() == OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT)
        {
            PsiFile psiFile = child.getPsi().getContainingFile();

            return builder.createDataLanguageRootBlock(child.getPsi(), null, getTopSettings(),
                    new HtmlPolicy(getTopSettings(), FormattingDocumentModelImpl.createOn(psiFile)), psiFile, childIndent);
        }

        return super.createBlock(child, wrap, childAlignment, childIndent, alignmentFactory, parentBlock);
    }

}
