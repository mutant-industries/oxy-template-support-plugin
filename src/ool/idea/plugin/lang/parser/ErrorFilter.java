package ool.idea.plugin.lang.parser;

import com.intellij.codeInsight.highlighting.TemplateLanguageErrorFilter;
import com.intellij.lang.html.HTMLLanguage;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;

/**
 * 2/12/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ErrorFilter extends TemplateLanguageErrorFilter
{
    protected ErrorFilter()
    {
        super(OxyTemplateParserDefinition.OPEN_BLOCK_MARKERS, OxyTemplateFileViewProvider.class,
                HTMLLanguage.INSTANCE.getDisplayName());
    }

}
