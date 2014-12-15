package ool.idea.macro.highlighter;

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
import ool.idea.macro.MacroSupportParserDefinition;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 7/25/14
 *
 * TODO celý špatně
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportHighlighter extends SyntaxHighlighterBase
{
    // ------------------------------------------
    public static final TextAttributesKey KEY = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey VALUE = createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey MACRO_TAG_BOUNDARY = createTextAttributesKey("SIMPLE_NEVIM", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MACRO_NAME_PREFIX = createTextAttributesKey("SIMPLE_NECO", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_NAME = createTextAttributesKey("SIMPLE_COSI", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MACRO_PARAM_NAME = createTextAttributesKey("SIMPLE_NO", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_PARAM_VALUE = createTextAttributesKey("SIMPLE_NO1", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey MACRO_PARAM_EXPRESSION_STATEMENT = createTextAttributesKey("SIMPLE_3NO", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
            new TextAttributes(Color.RED, null, null, null, Font.BOLD));
    // ------------------------------------------

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] MACRO_NAME_KEYS = new TextAttributesKey[]{MACRO_NAME};
    private static final TextAttributesKey[] MACRO_NAME_PREFIX_KEYS = new TextAttributesKey[]{MACRO_NAME_PREFIX};
    private static final TextAttributesKey[] XML_ELEMENT_KEYS = new TextAttributesKey[]{MACRO_TAG_BOUNDARY};
    private static final TextAttributesKey[] MACRO_PARAM_NAME_KEYS = new TextAttributesKey[]{MACRO_PARAM_NAME};
    private static final TextAttributesKey[] MACRO_PARAM_VALUE_KEYS = new TextAttributesKey[]{MACRO_PARAM_VALUE};
    private static final TextAttributesKey[] MACRO_PARAM_EXPRESSION_STATEMENT_KEYS = new TextAttributesKey[]{MACRO_PARAM_EXPRESSION_STATEMENT};
    // ------------------------------------------------------------------------------------
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    public Lexer getHighlightingLexer()
    {
        return new MacroSupportParserDefinition().createLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
    {
        if (tokenType.equals(MacroSupportTypes.DIRECTIVE_PARAM_BOUNDARY) || tokenType.equals(MacroSupportTypes.DIRECTIVE_PARAM))
            return VALUE_KEYS;

        else if (tokenType.equals(MacroSupportTypes.DIRECTIVE))
            return KEY_KEYS;

        else if (tokenType.equals(TokenType.BAD_CHARACTER))
            return BAD_CHAR_KEYS;

        else if (tokenType.equals(MacroSupportTypes.XML_TAG_END) || tokenType.equals(MacroSupportTypes.XML_TAG_START) ||
                tokenType.equals(MacroSupportTypes.XML_CLOSE_TAG_START) || tokenType.equals(MacroSupportTypes.XML_UNPAIRED_TAG_END))
            return XML_ELEMENT_KEYS;

        else if(tokenType.equals(MacroSupportTypes.MACRO_NAME))
            return MACRO_NAME_KEYS;

        else if(tokenType.equals(MacroSupportTypes.MACRO_XML_PREFIX))
            return MACRO_NAME_PREFIX_KEYS;

        else if(tokenType.equals(MacroSupportTypes.MACRO_PARAM_NAME))
            return MACRO_PARAM_NAME_KEYS;

        else if(tokenType.equals(MacroSupportTypes.MACRO_PARAM_BOUNDARY) || tokenType.equals(MacroSupportTypes.MACRO_PARAM))
            return MACRO_PARAM_VALUE_KEYS;

        else if(tokenType.equals(MacroSupportTypes.MACRO_PARAM_EXPRESSION_STATEMENT))
            return MACRO_PARAM_EXPRESSION_STATEMENT_KEYS;

        return EMPTY_KEYS;
    }
}
