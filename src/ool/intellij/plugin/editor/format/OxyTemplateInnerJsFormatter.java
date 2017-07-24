package ool.intellij.plugin.editor.format;

import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlockFactory;
import ool.intellij.plugin.lang.OxyTemplate;

import com.intellij.formatting.FormattingMode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageFormatting;
import com.intellij.lang.javascript.formatter.JSBlockContext;
import com.intellij.lang.javascript.formatter.JavascriptFormattingModelBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * 2/25/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsFormatter extends JavascriptFormattingModelBuilder
{
    private final AbstractXmlTemplateFormattingModelBuilder builder;

    public OxyTemplateInnerJsFormatter()
    {
        super();

        builder = (AbstractXmlTemplateFormattingModelBuilder) LanguageFormatting.INSTANCE.forLanguage(OxyTemplate.INSTANCE);
    }

    @NotNull
    @Override
    protected JSBlockContext createBlockFactory(CodeStyleSettings settings, Language dialect, FormattingMode mode)
    {
        return new InnerJsBlockFactory(builder, settings, dialect, null, mode);
    }

}
