package ool.idea.macro.lexer;

import java.io.IOException;
import ool.idea.macro.psi.MacroSupportTypes;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by mayrp on 12/13/14.
 */
public class InnerJsTest extends AbstractLexerTest
{
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

        lexer.start(input);

        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, nextToken());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER_PRINT, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, nextToken());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());
        assertEquals(MacroSupportTypes.OPEN_BLOCK_MARKER, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE, nextToken());
        assertEquals(MacroSupportTypes.CLOSE_BLOCK_MARKER, nextToken());
        assertEquals(MacroSupportTypes.TEMPLATE_HTML_CODE, nextToken());

        assertEquals(null, nextToken());
    }

}
