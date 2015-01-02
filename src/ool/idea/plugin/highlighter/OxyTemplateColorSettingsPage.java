package ool.idea.plugin.highlighter;

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
            new AttributesDescriptor("Inner block boundary marker", OxyTemplateHighlighter.BLOCK),
            new AttributesDescriptor("Directive", OxyTemplateHighlighter.KEY),
            new AttributesDescriptor("Directive param", OxyTemplateHighlighter.VALUE),
            new AttributesDescriptor("Macro tag boundary", OxyTemplateHighlighter.MACRO_TAG_BOUNDARY),
            new AttributesDescriptor("Macro name xml namespace", OxyTemplateHighlighter.MACRO_XML_NAMESPACE),
            new AttributesDescriptor("Macro name", OxyTemplateHighlighter.MACRO_NAME),
            new AttributesDescriptor("Macro parameter name", OxyTemplateHighlighter.MACRO_PARAM_NAME),
            new AttributesDescriptor("Macro parameter value", OxyTemplateHighlighter.MACRO_PARAM_VALUE),
            new AttributesDescriptor("Macro parameter expression statement", OxyTemplateHighlighter.MACRO_PARAM_EXPRESSION_STATEMENT),
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
        return new OxyTemplateHighlighter();
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
