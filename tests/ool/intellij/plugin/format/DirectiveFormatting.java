package ool.intellij.plugin.format;

/**
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DirectiveFormatting extends AbstractFormattingTest
{
    @Override
    protected String getTestDataDir()
    {
        return "directives";
    }

    public void testDirectiveBlock()
    {
        checkFormatting("directive_block");
    }

    public void testDirectiveInMacroTag()
    {
        checkFormatting("directive_in_macro_tag");
    }

    public void testDirectiveInXmlTag()
    {
        checkFormatting("directive_in_xml_tag");
    }

    public void testDirectiveInJsBlock()
    {
        checkFormatting("directive_in_js_block");
    }

}
