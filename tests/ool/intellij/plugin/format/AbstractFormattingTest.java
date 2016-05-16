package ool.intellij.plugin.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import java.io.File;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 3/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractFormattingTest extends LightCodeInsightFixtureTestCase
{
    @NonNls
    private static final String TEST_DATA_BASE_DIR = "testData";

    @NonNls
    private static final String FORMATTER_TEST_DATA_BASE_DIR = "format";

    @NonNls
    private static final String TEST_FILE_SUFFIX = ".jsm";

    @NonNls
    protected String expectedDataDir = "expected";

    @NonNls
    abstract protected String getTestDataDir();

    protected String getTestDataPath()
    {
        return TEST_DATA_BASE_DIR + File.separator + FORMATTER_TEST_DATA_BASE_DIR +
                File.separator + getTestDataDir() + File.separator;
    }

    protected String getExpectedDataPath()
    {
        return File.separator + expectedDataDir + File.separator;
    }

    protected void checkFormatting(@NotNull @NonNls final String file)
    {
        final String original = getTestDataPath() + file + TEST_FILE_SUFFIX;
        final String expected = getExpectedDataPath() + file + TEST_FILE_SUFFIX;

        CommandProcessor.getInstance().executeCommand(getProject(), () -> {
            myFixture.configureByFiles(original);

            ApplicationManager.getApplication().runWriteAction(() -> {
                CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
            });

            myFixture.checkResultByFile(expected);
        }, "reformat test action", "reformat test action" + System.nanoTime());
    }

}
