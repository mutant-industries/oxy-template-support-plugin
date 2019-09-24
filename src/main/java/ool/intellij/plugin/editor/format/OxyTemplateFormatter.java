package ool.intellij.plugin.editor.format;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ool.intellij.plugin.editor.format.block.OxyTemplateBlock;
import ool.intellij.plugin.editor.format.block.OxyTemplateForeignElementWrapper;
import ool.intellij.plugin.editor.format.block.xml.OxyTemplateXmlBlock;
import ool.intellij.plugin.editor.format.block.xml.OxyTemplateXmlSyntheticBlock;
import ool.intellij.plugin.editor.format.block.xml.OxyTemplateXmlTagBlock;
import ool.intellij.plugin.file.OxyTemplateFile;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.formatting.ASTBlock;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.SyntheticBlock;
import com.intellij.psi.formatter.xml.XmlBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.formatter.xml.XmlTagBlock;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFormatter extends AbstractXmlTemplateFormattingModelBuilder
{
    @Override
    protected boolean isTemplateFile(PsiFile psiFile)
    {
        return psiFile instanceof OxyTemplateFile;
    }

    @Override
    public boolean isOuterLanguageElement(PsiElement psiElement)
    {
        return psiElement.getNode().getElementType() == OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT;
    }

    @Override
    public boolean isMarkupLanguageElement(PsiElement psiElement)
    {
        return psiElement.getNode().getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE;
    }

    @Override
    protected Block createTemplateLanguageBlock(ASTNode astNode, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, Alignment alignment, Wrap wrap)
    {
        return new OxyTemplateBlock(this, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
    }

    @Override
    protected XmlTagBlock createXmlTagBlock(ASTNode astNode, Wrap wrap, Alignment alignment, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        return new OxyTemplateXmlTagBlock(this, astNode, wrap, alignment, xmlFormattingPolicy, indent);
    }

    @Override
    protected XmlBlock createXmlBlock(ASTNode astNode, Wrap wrap, Alignment alignment, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, TextRange textRange)
    {
        return new OxyTemplateXmlBlock(this, astNode, wrap, alignment, xmlFormattingPolicy, indent, textRange);
    }

    @Override
    protected SyntheticBlock createSyntheticBlock(List<Block> list, Block block, Indent indent, XmlFormattingPolicy xmlFormattingPolicy, Indent childIndent)
    {
        return new OxyTemplateXmlSyntheticBlock(list, block, indent, xmlFormattingPolicy, childIndent);
    }

    @Override
    public Block createDataLanguageRootBlock(PsiElement psiElement, Language language, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, PsiFile psiFile, Indent indent)
    {
        return new OxyTemplateForeignElementWrapper(this, psiElement.getNode(), null, null, codeStyleSettings, xmlFormattingPolicy, indent);
    }

    @Override
    public List<Block> mergeWithTemplateBlocks(List<Block> list, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent)
    {
        List<Block> rearrangedBlocks = new ArrayList<>(10);
        OxyTemplateForeignElementWrapper firstForeignBlock = null;
        Block lastForeignBlock = null;
        List<Block> afterLastForeignBlock = new LinkedList<>();

        for (Block block : list)
        {
            if (block instanceof OxyTemplateForeignElementWrapper)
            {
                afterLastForeignBlock.clear();
                lastForeignBlock = block;

                if (firstForeignBlock == null)
                {
                    firstForeignBlock = (OxyTemplateForeignElementWrapper) block;
                    firstForeignBlock.setIndent(indent);
                }
            }
            else if (firstForeignBlock == null)
            {
                rearrangedBlocks.add(block);
            }
            // js / xml macro formatting has precedence over html formatting
            else if (block.isIncomplete() || block instanceof ASTBlock && OxyTemplateHelper.containsElement(((ASTBlock) block)
                    .getNode().getPsi(), OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT))
            {
                afterLastForeignBlock.clear();
                lastForeignBlock = block;
            }
            else
            {
                afterLastForeignBlock.add(block);
            }
        }

        if (firstForeignBlock != null)
        {
            firstForeignBlock.setTextRange(TextRange.create(firstForeignBlock.getTextRange().getStartOffset(),
                    lastForeignBlock.getTextRange().getEndOffset()));

            rearrangedBlocks.addAll(firstForeignBlock.getSubBlocks());
            rearrangedBlocks.addAll(afterLastForeignBlock);
        }

        return rearrangedBlocks;
    }

}
