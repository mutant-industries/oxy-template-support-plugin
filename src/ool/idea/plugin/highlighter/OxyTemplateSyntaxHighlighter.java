package ool.idea.plugin.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import java.awt.Color;
import java.awt.Font;
import ool.idea.plugin.file.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateSyntaxHighlighter extends SyntaxHighlighterBase
{
    // ------------------------------------------
    public static final TextAttributesKey KEY = createTextAttributesKey("OXY_TEMPLATE_KEY", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey VALUE = createTextAttributesKey("OXY_TEMPLATE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BLOCK = createTextAttributesKey("OXY_TEMPLATE_BLOCK", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MACRO_TAG_BOUNDARY = createTextAttributesKey("OXY_TEMPLATE_MACRO_TAG_BOUNDARY", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MACRO_XML_NAMESPACE = createTextAttributesKey("OXY_TEMPLATE_MACRO_XML_NAMESPACE", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_NAME = createTextAttributesKey("OXY_TEMPLATE_MACRO_NAME", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MACRO_PARAM_NAME = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_NAME", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_PARAM_VALUE = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_VALUE", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_PARAM_EXPRESSION_STATEMENT = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_EXPRESSION_STATEMENT", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("OXY_TEMPLATE_BAD_CHARACTER",
            new TextAttributes(Color.RED, null, null, null, Font.BOLD));
    // ------------------------------------------

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] BLOCK_KEYS = new TextAttributesKey[]{BLOCK};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] MACRO_NAME_KEYS = new TextAttributesKey[]{MACRO_NAME};
    private static final TextAttributesKey[] MACRO_XML_NAMESPACE_KEYS = new TextAttributesKey[]{MACRO_XML_NAMESPACE};
    private static final TextAttributesKey[] XML_ELEMENT_KEYS = new TextAttributesKey[]{MACRO_TAG_BOUNDARY};
    private static final TextAttributesKey[] MACRO_PARAM_NAME_KEYS = new TextAttributesKey[]{MACRO_PARAM_NAME};
    private static final TextAttributesKey[] MACRO_PARAM_VALUE_KEYS = new TextAttributesKey[]{MACRO_PARAM_VALUE};
    private static final TextAttributesKey[] MACRO_PARAM_EXPRESSION_STATEMENT_KEYS = new TextAttributesKey[]{MACRO_PARAM_EXPRESSION_STATEMENT};
    // ------------------------------------------------------------------------------------
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    public Lexer getHighlightingLexer()
    {
        return new OxyTemplateParserDefinition().createLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
    {
        if (tokenType.equals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY) || tokenType.equals(OxyTemplateTypes.T_DIRECTIVE_PARAM))
        {
            return VALUE_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_DIRECTIVE))
        {
            return KEY_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER) || tokenType.equals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_PRINT)
                || tokenType.equals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE) || tokenType.equals(OxyTemplateTypes.T_CLOSE_BLOCK_MARKER))
        {
            return BLOCK_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_XML_CLOSE_TAG_END) || tokenType.equals(OxyTemplateTypes.T_XML_OPEN_TAG_END)
                || tokenType.equals(OxyTemplateTypes.T_XML_TAG_START) || tokenType.equals(OxyTemplateTypes.T_XML_CLOSE_TAG_START)
                || tokenType.equals(OxyTemplateTypes.T_XML_UNPAIRED_TAG_END))
        {
            return XML_ELEMENT_KEYS;
        }
        else if(tokenType.equals(OxyTemplateTypes.T_MACRO_NAME))
        {
            return MACRO_NAME_KEYS;
        }
        else if(tokenType.equals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE))
        {
            return MACRO_XML_NAMESPACE_KEYS;
        }
        else if(tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_NAME))
        {
            return MACRO_PARAM_NAME_KEYS;
        }
        else if(tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY) || tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM))
        {
            return MACRO_PARAM_VALUE_KEYS;
        }
        else if(tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT))
        {
            return MACRO_PARAM_EXPRESSION_STATEMENT_KEYS;
        }
        else if (tokenType.equals(TokenType.BAD_CHARACTER))
        {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

}
