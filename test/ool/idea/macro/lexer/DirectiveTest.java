package ool.idea.macro.lexer;

import java.io.IOException;
import ool.idea.macro.MacroSupportLexer;
import static ool.idea.macro.psi.MacroSupportTypes.*;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class DirectiveTest
{
    private MacroSupportLexer lexer;

    @Before
    public void initLexer()
    {
        lexer = new MacroSupportLexer();
    }

    @Test
    public void directiveBlockTest() throws IOException
    {
        String input = "<%@ layout \"_layout.jsm\" %>";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(DIRECTIVE_PARAM, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(CLOSE_BLOCK_MARKER, lexer.advance());
        assertEquals(null, lexer.advance());
    }

    @Test
    public void directiveBlockUnclosedParameterTest() throws IOException
    {
        String input = "<%@ layout \"_layout.jsm %>";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(DIRECTIVE_PARAM, lexer.advance());
        assertEquals(null, lexer.advance());
    }

    @Test
    public void directiveBlockUnclosedBlockTest() throws IOException
    {
        String input = "<%@ layout \"_layout.jsm\" %";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(DIRECTIVE_PARAM, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(BAD_CHARACTER, lexer.advance());
        assertEquals(null, lexer.advance());

        input = "<%@ layout \"_layout.jsm\" ...";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(DIRECTIVE_PARAM, lexer.advance());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(BAD_CHARACTER, lexer.advance());
        assertEquals(TEMPLATE_HTML_CODE, lexer.advance());
        assertEquals(null, lexer.advance());
    }

}
