package ool.idea.plugin.editor.format;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageFormatting;
import com.intellij.lang.javascript.formatter.JavascriptFormattingModelBuilder;
import com.intellij.lang.javascript.formatter.blocks.JSBlock;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import ool.idea.plugin.editor.format.block.OxyTemplateForeignElementWrapper;
import ool.idea.plugin.editor.format.block.innerJs.InnerJsBlock;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
* 2/25/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class OxyTemplateInnerJsFormatter extends JavascriptFormattingModelBuilder
{
    protected final AbstractTemplateLanguageFormattingModelBuilder builder;

    public OxyTemplateInnerJsFormatter()
    {
        builder = (AbstractTemplateLanguageFormattingModelBuilder) LanguageFormatting.INSTANCE.forLanguage(OxyTemplate.INSTANCE);
    }

    @Override
    public JSBlock createSubBlock(@NotNull ASTNode child, Alignment childAlignment, Indent childIndent, Wrap wrap,
                                  @NotNull CodeStyleSettings topSettings, @NotNull Language dialect)
    {
        if(child.getElementType() == OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT)
        {
            return new OxyTemplateForeignElementWrapper(builder, child, wrap, childAlignment, topSettings,
                    new XmlPolicy(topSettings, null), childIndent);
        }
        else
        {
            return new InnerJsBlock(child, childAlignment, childIndent, wrap, topSettings, dialect);
        }
    }

}
