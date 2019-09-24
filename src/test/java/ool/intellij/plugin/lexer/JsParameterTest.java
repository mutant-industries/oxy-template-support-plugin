package ool.intellij.plugin.lexer;

import java.io.IOException;

import ool.intellij.plugin.psi.OxyTemplateTypes;

import org.junit.Test;

import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.junit.Assert.assertEquals;

/**
 * 2/7/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsParameterTest extends AbstractLexerTest
{
    @Test
    public void javascriptParameterTest() throws IOException
    {
        String input = "<m:oxy.repeat name=\"test\" varName=\"params\" indexName=\"i\">";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());

        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_OPEN_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

}
