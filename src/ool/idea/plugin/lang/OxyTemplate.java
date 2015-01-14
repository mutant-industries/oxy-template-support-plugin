package ool.idea.plugin.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.templateLanguages.TemplateLanguage;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplate extends Language implements TemplateLanguage
{
    public static final OxyTemplate INSTANCE = new OxyTemplate();

    private OxyTemplate()
    {
        super("OxyTemplate", "application/x-oxy-template");
    }

    public static LanguageFileType getDefaultTemplateLang()
    {
        return StdFileTypes.HTML;
    }

}
