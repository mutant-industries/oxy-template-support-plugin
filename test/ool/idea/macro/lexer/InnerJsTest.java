package ool.idea.macro.lexer;

import java.io.IOException;
import ool.idea.macro.MacroSupportLexer;
import ool.idea.macro.psi.MacroSupportTypes;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class InnerJsTest
{
    private MacroSupportLexer lexer;

    @Before
    public void initLexer()
    {
        lexer = new MacroSupportLexer();
    }

    @Test
    public void blockTest() throws IOException
    {
        String input =
                "<div>\n" +
                "    <ul>\n" +
                "        <% for(var i=0; i<supplies.length; i%10) { %>\n" +
                "            <li class=\"li\">\n" +
                "                <a href=\"neco\" title=\"nevim\">\n" +
                "                    <%= supplies[i]; %>\n" +
                "                </a>\n" +
                "            </li>\n" +
                "        <% } %>\n" +
                "    </ul>\n" +
                "</div>\n";

        lexer.reset(input, 0, input.length(), 0);

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER_PRINT, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, lexer.advance());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, lexer.advance());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, lexer.advance());

        assertEquals(null, lexer.advance());
    }

}
