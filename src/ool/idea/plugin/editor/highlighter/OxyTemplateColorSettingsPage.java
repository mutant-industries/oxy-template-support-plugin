package ool.idea.plugin.editor.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import java.util.Map;
import javax.swing.Icon;
import ool.idea.plugin.file.OxyTemplateFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/12/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateColorSettingsPage implements ColorSettingsPage
{
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
            new AttributesDescriptor("Inner block boundary marker", OxyTemplateSyntaxHighlighter.BLOCK),
            new AttributesDescriptor("Directive", OxyTemplateSyntaxHighlighter.KEY),
            new AttributesDescriptor("Directive param", OxyTemplateSyntaxHighlighter.VALUE),
            new AttributesDescriptor("Macro tag boundary", OxyTemplateSyntaxHighlighter.MACRO_TAG_BOUNDARY),
            new AttributesDescriptor("Macro name xml namespace", OxyTemplateSyntaxHighlighter.MACRO_XML_NAMESPACE),
            new AttributesDescriptor("Macro name", OxyTemplateSyntaxHighlighter.MACRO_NAME),
            new AttributesDescriptor("Macro parameter name", OxyTemplateSyntaxHighlighter.MACRO_PARAM_NAME),
            new AttributesDescriptor("Macro parameter value", OxyTemplateSyntaxHighlighter.MACRO_PARAM_VALUE),
            new AttributesDescriptor("Macro parameter expression statement", OxyTemplateSyntaxHighlighter.MACRO_PARAM_EXPRESSION_STATEMENT),
    };

    @Nullable
    @Override
    public Icon getIcon()
    {
        return OxyTemplateFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter()
    {
        return new OxyTemplateSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText()
    {
        return "<%@ layout \"_layout.jst\" %>\n" +
                "\n" +
                "<m:foo.bar asdf=\"expr: __js__\">\n" +
                "    <m:bar.baz param_key=\"value\" />\n" +
                "</m:foo.bar>\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
    {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors()
    {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors()
    {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Oxy Template";
    }

}
