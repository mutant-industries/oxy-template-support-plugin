package ool.idea.macro.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import java.util.Map;
import javax.swing.Icon;
import ool.idea.macro.file.MacroSupportFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/12/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportColorSettingsPage implements ColorSettingsPage
{
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Directive", MacroSupportHighlighter.KEY),
            new AttributesDescriptor("Directive param", MacroSupportHighlighter.VALUE),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return MacroSupportFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new MacroSupportHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "<%@ layout \"_layout.jst\" %>\n" +
                "<%@ include_once \"../macros/redesign/layout/layout.jsm\" %>\n" +
                "\n" +
                "<div>\n" +
                "    <ul>\n" +
                "        <% for(var i=0; i<supplies.length; i++) { %>\n" +
                "            <li class=\"li\">\n" +
                "                <a href=\"neco\" title=\"nevim\">\n" +
                "                    <% supplies[i] %>\n" +
                "                </a>\n" +
                "            </li>\n" +
                "        <% } %>\n" +
                "    </ul>\n" +
                "</div>\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Oxy Template";
    }
}
