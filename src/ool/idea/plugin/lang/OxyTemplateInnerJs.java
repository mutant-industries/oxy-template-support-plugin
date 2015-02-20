package ool.idea.plugin.lang;

import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.JSLanguageDialect;

/**
 * 1/12/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJs extends JSLanguageDialect
{
    public static final OxyTemplateInnerJs INSTANCE = new OxyTemplateInnerJs();

    protected OxyTemplateInnerJs()
    {
        super("OxyTemplateInnerJs", DialectOptionHolder.OTHER);
    }

    @Override
    public String getFileExtension()
    {
        return "js";
    }

}
