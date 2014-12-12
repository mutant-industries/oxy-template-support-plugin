package ool.idea.macro.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import java.awt.Color;
import java.awt.Font;
import ool.idea.macro.MacroSupportParserDefinition;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportHighlighter extends SyntaxHighlighterBase
{
    // directive name
    public static final TextAttributesKey KEY = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.LABEL);
    // directive parameter
    public static final TextAttributesKey VALUE = createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
            new TextAttributes(Color.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    public MacroSupportHighlighter(/*@Nullable Project project*/)
    {

    }

    @NotNull
    public Lexer getHighlightingLexer()
    {
        return new MacroSupportParserDefinition().createLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
    {
        if (tokenType.equals(MacroSupportTypes.DIRECTIVE_PARAM_BOUNDARY) || tokenType.equals(MacroSupportTypes.DIRECTIVE_PARAM)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(MacroSupportTypes.DIRECTIVE)) {
            return KEY_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }
}
