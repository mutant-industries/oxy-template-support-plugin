package ool.idea.plugin.format;

/**
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlFormatting extends AbstractFormattingTest
{
    @Override
    protected String getTestDataDir()
    {
        return "xml";
    }

    public void testIndentAndSpacing()
    {
        checkFormatting("indent_spacing");
    }

    public void testIndentInXmlText1()
    {
        checkFormatting("indent_xmltext_1");
    }

    public void testIndentInXmlText2()
    {
        checkFormatting("indent_xmltext_2");
    }

    public void testMacroParamIndent()
    {
        checkFormatting("macro_param_indent");
    }

}
