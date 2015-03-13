package ool.idea.plugin.format;

/**
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class Ranges extends AbstractFormattingTest
{
    @Override
    protected String getTestDataDir()
    {
        return "ranges";
    }

    public void testJsXmlText1()
    {
        checkFormatting("js_xmltext_1");
    }

    public void testJsXmlText2()
    {
        checkFormatting("js_xmltext_2");
    }

    public void testJsXmlText3()
    {
        checkFormatting("js_xmltext_3");
    }

    public void testMacroTagsXmlParam()
    {
        checkFormatting("macro_tags_xmlparam");
    }

    public void testMacroTagsXmlTextXmlComment1()
    {
        checkFormatting("macro_tags_xmltext_xmlcomment_1");
    }

    public void testMacroTagsXmlTextXmlComment2()
    {
        checkFormatting("macro_tags_xmltext_xmlcomment_2");
    }

    public void testXmlMergeBlocks()
    {
        checkFormatting("xml_blocks_merge_test");
    }

}
