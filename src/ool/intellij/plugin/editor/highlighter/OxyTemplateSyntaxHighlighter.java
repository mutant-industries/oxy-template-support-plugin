package ool.intellij.plugin.editor.highlighter;

import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateSyntaxHighlighter extends SyntaxHighlighterBase
{
    // ------------------------------------------
    static final TextAttributesKey KEY = createTextAttributesKey("OXY_TEMPLATE_KEY", DefaultLanguageHighlighterColors.LABEL);
    static final TextAttributesKey VALUE = createTextAttributesKey("OXY_TEMPLATE_VALUE", DefaultLanguageHighlighterColors.STRING);
    static final TextAttributesKey COMMENT = createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    static final TextAttributesKey BLOCK = createTextAttributesKey("OXY_TEMPLATE_BLOCK", DefaultLanguageHighlighterColors.MARKUP_TAG);
    static final TextAttributesKey MACRO_TAG_BOUNDARY = createTextAttributesKey("OXY_TEMPLATE_MACRO_TAG_BOUNDARY", DefaultLanguageHighlighterColors.MARKUP_TAG);
    static final TextAttributesKey MACRO_XML_NAMESPACE = createTextAttributesKey("OXY_TEMPLATE_MACRO_XML_NAMESPACE", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey MACRO_NAME = createTextAttributesKey("OXY_TEMPLATE_MACRO_NAME", DefaultLanguageHighlighterColors.MARKUP_TAG);
    static final TextAttributesKey MACRO_PARAM_NAME = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_NAME", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey MACRO_PARAM_VALUE = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_VALUE", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey MACRO_PARAM_VALUE_ENCODED_ENTITY = createTextAttributesKey("MACRO_PARAM_VALUE_ENCODED_ENTITY", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey MACRO_PARAM_EXPRESSION_STATEMENT = createTextAttributesKey("OXY_TEMPLATE_MACRO_PARAM_EXPRESSION_STATEMENT", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("OXY_TEMPLATE_BAD_CHARACTER", CodeInsightColors.ERRORS_ATTRIBUTES);
    // ------------------------------------------

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] BLOCK_KEYS = new TextAttributesKey[]{BLOCK};
    private static final TextAttributesKey[] MACRO_NAME_KEYS = new TextAttributesKey[]{MACRO_NAME};
    private static final TextAttributesKey[] MACRO_XML_NAMESPACE_KEYS = new TextAttributesKey[]{MACRO_XML_NAMESPACE};
    private static final TextAttributesKey[] XML_ELEMENT_KEYS = new TextAttributesKey[]{MACRO_TAG_BOUNDARY};
    private static final TextAttributesKey[] MACRO_PARAM_NAME_KEYS = new TextAttributesKey[]{MACRO_PARAM_NAME};
    private static final TextAttributesKey[] MACRO_PARAM_VALUE_ENCODED_ENTITY_KEYS = new TextAttributesKey[]{MACRO_PARAM_VALUE_ENCODED_ENTITY};
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
        if (tokenType.equals(OxyTemplateTypes.T_BLOCK_COMMENT))
        {
            return COMMENT_KEYS;
        }
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
                || tokenType.equals(OxyTemplateTypes.T_XML_EMPTY_TAG_END))
        {
            return XML_ELEMENT_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_MACRO_NAME))
        {
            return MACRO_NAME_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE))
        {
            return MACRO_XML_NAMESPACE_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_NAME))
        {
            return MACRO_PARAM_NAME_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY) || tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM))
        {
            return MACRO_PARAM_VALUE_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_XML_ENCODED_ENTITY))
        {
            return MACRO_PARAM_VALUE_ENCODED_ENTITY_KEYS;
        }
        else if (tokenType.equals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT))
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
