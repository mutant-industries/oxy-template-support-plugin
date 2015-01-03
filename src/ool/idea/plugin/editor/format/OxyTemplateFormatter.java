package ool.idea.plugin.editor.format;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.psi.OxyTemplateTypes;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFormatter extends AbstractTemplateLanguageFormattingModelBuilder
{
    @Override
    protected boolean isTemplateFile(PsiFile psiFile)
    {
        return psiFile instanceof OxyTemplateFile;
    }

    @Override
    public boolean isOuterLanguageElement(PsiElement psiElement)
    {
        return  psiElement.getNode().getElementType() == OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT;
    }

    @Override
    public boolean isMarkupLanguageElement(PsiElement psiElement)
    {
        return psiElement.getNode().getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE;
    }

    @Override
    protected Block createTemplateLanguageBlock(ASTNode astNode, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, Alignment alignment, Wrap wrap)
    {
        return new OxyTemplateLanguageBlock(this, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
    }

}
