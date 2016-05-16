package ool.intellij.plugin.format;

/**
 * Expected result verification isn't really important. What matters is that all ranges are properly set
 * and no exception is thrown.
 *
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnstableStates extends AbstractFormattingTest
{
    @Override
    protected String getTestDataDir()
    {
        return "unstable";
    }

    public void testUnclosedJsBlocks()
    {
        checkFormatting("unclosed_js_blocks");
    }

    public void testUnclosedMacroTags1()
    {
        checkFormatting("unclosed_macro_tags_1");
    }

    public void testUnclosedMacroTags2()
    {
        checkFormatting("unclosed_macro_tags_2");
    }

    public void testUnclosedMacroTags3()
    {
        checkFormatting("unclosed_macro_tags_3");
    }

    public void testUnclosedTags1()
    {
        checkFormatting("unclosed_tags_1");
    }

    public void testUnclosedTags2()
    {
        checkFormatting("unclosed_tags_2");
    }

    public void testUnclosedXmlTags1()
    {
        checkFormatting("unclosed_xml_tags_1");
    }

    public void testUnclosedXmlTags2()
    {
        checkFormatting("unclosed_xml_tags_2");
    }

    public void testUnclosedXmlTags3()
    {
        checkFormatting("unclosed_xml_tags_3");
    }

    public void testUnclosedXmlTags4()
    {
        checkFormatting("unclosed_xml_tags_4");
    }

}
