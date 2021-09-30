package ool.intellij.plugin.lang.parser;

import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;

import com.intellij.codeInsight.highlighting.TemplateLanguageErrorFilter;
import com.intellij.lang.html.HTMLLanguage;

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
