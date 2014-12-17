package ool.idea.macro.lexer;

import java.io.IOException;
import static ool.idea.macro.psi.MacroSupportTypes.*;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class DirectiveTest extends AbstractLexerTest
{
    @Test
    public void directiveBlockTest() throws IOException
    {
        String input = "<%@ include_once \"_layout.jsm\" %>";
        lexer.start(input);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(DIRECTIVE_PARAM, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedParameterTest() throws IOException
    {
        String input = "<%@ unknown \"_layout.jsm %>";
        lexer.start(input);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(DIRECTIVE_PARAM, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedBlockTest() throws IOException
    {
        String input = "<%@ include \"_layout.jsm\" %";
        lexer.start(input);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(DIRECTIVE_PARAM, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(BAD_CHARACTER, nextToken());
        assertEquals(null, nextToken());

        input = "<%@ layout \"_layout.jsm\" ...";
        lexer.start(input);

        assertEquals(OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(DIRECTIVE_PARAM, nextToken());
        assertEquals(DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(BAD_CHARACTER, nextToken());
        assertEquals(TEMPLATE_HTML_CODE, nextToken());
        assertEquals(null, nextToken());
    }

}
