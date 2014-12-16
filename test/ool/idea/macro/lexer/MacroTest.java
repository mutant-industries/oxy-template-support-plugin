package ool.idea.macro.lexer;

import java.io.IOException;
import ool.idea.macro.psi.MacroSupportTypes;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class MacroTest extends AbstractLexerTest
{
    @Test
    public void macroCloseTagTest() throws IOException
    {
        String input = "</m:foo.bar>";
        lexer.start(input);

        assertEquals(MacroSupportTypes.XML_CLOSE_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(MacroSupportTypes.XML_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void macroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\">";
        lexer.start(input);

        assertEquals(MacroSupportTypes.XML_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(MacroSupportTypes.XML_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void unpairedMacroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\" />";
        lexer.start(input);

        assertEquals(MacroSupportTypes.XML_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void expressionStatementTest() throws IOException
    {
        String input = "<m:oxy.ifTrue value=\"expr: true\" />";

        lexer.start(input);

        assertEquals(MacroSupportTypes.XML_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, nextToken());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());

        assertEquals(MacroSupportTypes.MACRO_PARAM_EXPRESSION_STATEMENT, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, nextToken());

        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(null, nextToken());
    }

    @Test
    public void macroHtmlMixTest() throws IOException
    {
        String input =
                "<div>\n" +
                "    <m:bar.baz>\n" +
                "        <li>\n" +
                "            <m:foo.bar />\n" +
                "        </li>\n" +
                "    </m:bar.baz>\n" +
                "</div>\n";

        lexer.start(input);

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());
        assertEquals(MacroSupportTypes.XML_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(MacroSupportTypes.XML_TAG_END, nextToken());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());

        assertEquals(MacroSupportTypes.XML_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(WHITE_SPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, nextToken());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());

        assertEquals(MacroSupportTypes.XML_CLOSE_TAG_START, nextToken());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, nextToken());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, nextToken());
        assertEquals(MacroSupportTypes.MACRO_NAME, nextToken());
        assertEquals(MacroSupportTypes.XML_TAG_END, nextToken());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());

        assertEquals(null, nextToken());
    }

}
