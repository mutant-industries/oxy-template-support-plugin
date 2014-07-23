package ool.idea.macro;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.templateLanguages.TemplateLanguage;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupport  extends Language implements TemplateLanguage
{
    private static final MacroSupport INSTANCE = new MacroSupport();

    private MacroSupport()
    {
        super("MacroSupport", "application/x-oxy-template");
    }

    public static MacroSupport getInstance()
    {
        return INSTANCE;
    }

    public static LanguageFileType getDefaultTemplateLang() {
        return StdFileTypes.HTML;
    }

}
