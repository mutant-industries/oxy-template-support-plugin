package ool.idea.plugin.format;

/**
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsFormatting extends AbstractFormattingTest
{
    @Override
    protected String getTestDataDir()
    {
        return "js";
    }

    public void testMarkerAlignment()
    {
        checkFormatting("marker_alignment");
    }

    public void testIndentAndSpacing1()
    {
        checkFormatting("indent_spacing_1");
    }

    public void testIndentAndSpacing2()
    {
        checkFormatting("indent_spacing_2");
    }

    public void testIndentAndSpacing3()
    {
        checkFormatting("indent_spacing_3");
    }

    public void testIndentAndSpacing4()
    {
        checkFormatting("indent_spacing_4");
    }

    public void testIndentAndSpacing5()
    {
        checkFormatting("indent_spacing_5");
    }

    public void testIndentAndSpacing6()
    {
        checkFormatting("indent_spacing_6");
    }

    public void testCompositeRoot1()
    {
        checkFormatting("composite_root_1");
    }

    public void testCompositeRoot2()
    {
        checkFormatting("composite_root_2");
    }

}
