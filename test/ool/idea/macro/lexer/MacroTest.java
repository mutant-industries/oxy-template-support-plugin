package ool.idea.macro.lexer;

import java.io.IOException;
import ool.idea.macro.MacroSupportLexer;
import ool.idea.macro.psi.MacroSupportTypes;
import static com.intellij.psi.TokenType.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class MacroTest
{
    private MacroSupportLexer lexer;

    @Before
    public void initLexer()
    {
        lexer = new MacroSupportLexer();
    }

    @Test
    public void macroCloseTagTest() throws IOException
    {
        String input = "</m:foo.bar>";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.XML_CLOSE_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.XML_TAG_END, lexer.advance());

        assertEquals(null, lexer.advance());
    }

    @Test
    public void macroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\">";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.XML_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());
        assertEquals(MacroSupportTypes.XML_TAG_END, lexer.advance());

        assertEquals(null, lexer.advance());
    }

    @Test
    public void unpairedMacroWithParamTest() throws IOException
    {
        String input = "<m:foo.bar param_name=\"param_value\" />";
        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.XML_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, lexer.advance());

        assertEquals(null, lexer.advance());
    }

    @Test
    public void expressionStatementTest() throws IOException
    {
        String input = "<m:oxy.ifTrue value=\"expr: true\" />";

        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.XML_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_ASSIGNMENT, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());

        assertEquals(MacroSupportTypes.MACRO_PARAM_EXPRESSION_STATEMENT, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, lexer.advance());

        assertEquals(MacroSupportTypes.MACRO_PARAM_BOUNDARY, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, lexer.advance());

        assertEquals(null, lexer.advance());
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

        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.XML_TAG_END, lexer.advance());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());

        assertEquals(MacroSupportTypes.XML_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(WHITE_SPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_UNPAIRED_TAG_END, lexer.advance());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());

        assertEquals(MacroSupportTypes.XML_CLOSE_TAG_START, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_XML_NAMESPACE, lexer.advance());
        assertEquals(MacroSupportTypes.XML_NAMESPACE_DELIMITER, lexer.advance());
        assertEquals(MacroSupportTypes.MACRO_NAME, lexer.advance());
        assertEquals(MacroSupportTypes.XML_TAG_END, lexer.advance());

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());

        assertEquals(null, lexer.advance());
    }

}
