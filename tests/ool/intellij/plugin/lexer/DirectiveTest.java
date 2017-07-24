package ool.intellij.plugin.lexer;

import java.io.IOException;

import ool.intellij.plugin.psi.OxyTemplateTypes;

import org.junit.Assert;
import org.junit.Test;

import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.junit.Assert.assertEquals;

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

        Assert.assertEquals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedParameterTest() throws IOException
    {
        String input = "<%@ unknown \"_layout.jsm %>";
        lexer.start(input);

        Assert.assertEquals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM, nextToken());
        assertEquals(null, nextToken());
    }

    @Test
    public void directiveBlockUnclosedBlockTest() throws IOException
    {
        String input = "<%@ include \"_layout.jsm\" %";
        lexer.start(input);

        Assert.assertEquals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());
        assertEquals(null, nextToken());

        input = "<%@ layout \"_layout.jsm\" ...";
        lexer.start(input);

        Assert.assertEquals(OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        Assert.assertEquals(OxyTemplateTypes.T_TEMPLATE_HTML_CODE, nextToken());
        assertEquals(null, nextToken());
    }

}
