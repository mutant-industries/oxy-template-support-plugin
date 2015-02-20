package ool.idea.plugin.formatter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.apache.commons.net.ntp.TimeStamp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class FormatterTest extends LightCodeInsightFixtureTestCase
{
    @NonNls
    private static final String TEST_DATA_PATH = "testData/";

    @NonNls
    private static final String FORMATTER_TEST_DIR = TEST_DATA_PATH + "formatter/";

    @NonNls
    private static final String FORMATTER_EXPECTED_DIR = "formatter/expected/";

    public void testHtmlMacroMix1()
    {
        checkFormatting("html_macro_mix_1.jsm");
    }

    public void testHtmlMacroMix2()
    {
        checkFormatting("html_macro_mix_2.jsm");
    }

    public void testHmlMacroMix3()
    {
        checkFormatting("html_macro_mix_3.jsm");
    }

    public void testHtmlMacroMix4()
    {
        checkFormatting("html_macro_mix_4.jsm");
    }

    public void testUnclosedTags1()
    {
        checkFormatting("unclosed_tags_1.jsm");
    }

    public void testUnclosedTags2()
    {
        checkFormatting("unclosed_tags_2.jsm");
    }

    public void testDirective()
    {
        checkFormatting("directive.jsm");
    }

    private void checkFormatting(@NotNull @NonNls final String file)
    {
        final String original = FORMATTER_TEST_DIR + file;
        final String expected = FORMATTER_EXPECTED_DIR + file;

        CommandProcessor.getInstance().executeCommand(getProject(), new Runnable()
        {
            @Override
            public void run()
            {
                myFixture.configureByFiles(original);

                ApplicationManager.getApplication().runWriteAction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
                    }
                });

                myFixture.checkResultByFile(expected);
            }
        }, "reformat test action", "reformat test action" + TimeStamp.getCurrentTime());
    }

}
