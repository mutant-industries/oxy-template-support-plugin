package ool.idea.plugin.lexer;

import static com.intellij.psi.TokenType.WHITE_SPACE;
import java.io.IOException;
import ool.idea.plugin.psi.OxyTemplateTypes;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 2/28/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ParameterQuoteTest extends AbstractLexerTest
{

    @Test
    public void singleQuoteTest() throws IOException
    {
        String input = "<m:foo param='value'/>";

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
        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void doubleQuoteTest() throws IOException
    {
        String input = "<m:foo param=\"value\"/>";

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
        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void singleQuoteJsTest() throws IOException
    {
        String input = "<m:foo param='expr: value'/>";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void doubleQuoteJsTest() throws IOException
    {
        String input = "<m:foo param=\"expr: value\"/>";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    // mismatched quotes test ----------------------------------------------------
    @Test
    public void mismatchedSingleQuoteTest() throws IOException
    {
        String input = "<m:foo param='value\"/>";

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

        assertEquals(null, nextToken());
    }

    @Test
    public void mismatchedDoubleQuoteTest() throws IOException
    {
        String input = "<m:foo param=\"value'/>";

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

        assertEquals(null, nextToken());
    }

    @Test
    public void mismatchedSingleQuoteJsTest() throws IOException
    {
        String input = "<m:foo param='expr: value\"/>";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void mismatchedDoubleQuoteJsTest() throws IOException
    {
        String input = "<m:foo param=\"expr: value'/>";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(null, nextToken());
    }

    // encoded entities ----------------------------------------------------------
    @Test
    public void encodedEntityTest1() throws IOException
    {
        String input = "<m:foo param=\"val&quot;ue\"/>";

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
        assertEquals(OxyTemplateTypes.T_XML_ENCODED_ENTITY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void encodedEntityTest2() throws IOException
    {
        String input = "<m:foo param=\"value&quot;\"/>";

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
        assertEquals(OxyTemplateTypes.T_XML_ENCODED_ENTITY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void encodedEntityTest3() throws IOException
    {
        String input = "<m:foo param=\"&quot;value\"/>";

        lexer.start(input);

        assertEquals(OxyTemplateTypes.T_XML_TAG_START, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_XML_NAMESPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_NAME, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_ENCODED_ENTITY, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM, nextToken());
        assertEquals(OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(OxyTemplateTypes.T_XML_EMPTY_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }



}
