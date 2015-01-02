package ool.idea.plugin.lexer;

import java.io.IOException;
import static ool.idea.plugin.psi.OxyTemplateTypes.*;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 12/13/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DirectiveTest extends AbstractLexerTest
{
    @Test
    public void directiveBlockTest() throws IOException
    {
        String input = "<%@ include_once \"_layout.jsm\" %>";
        lexer.start(input);

        assertEquals(T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(T_DIRECTIVE_PARAM, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedParameterTest() throws IOException
    {
        String input = "<%@ unknown \"_layout.jsm %>";
        lexer.start(input);

        assertEquals(T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(T_DIRECTIVE_PARAM, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedBlockTest() throws IOException
    {
        String input = "<%@ include \"_layout.jsm\" %";
        lexer.start(input);

        assertEquals(T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(T_DIRECTIVE_PARAM, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_TEMPLATE_HTML_CODE, nextToken());
        assertEquals(null, nextToken());

        input = "<%@ layout \"_layout.jsm\" ...";
        lexer.start(input);

        assertEquals(T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(T_DIRECTIVE_PARAM, nextToken());
        assertEquals(T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(T_TEMPLATE_HTML_CODE, nextToken());
        assertEquals(null, nextToken());
    }

}
