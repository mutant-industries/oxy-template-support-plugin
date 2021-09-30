package ool.intellij.plugin.psi;

import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataModifications;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 2/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsElementType extends TemplateDataElementType
{
    public OxyTemplateInnerJsElementType(@NonNls String debugName, Language language, IElementType templateElementType,
                                         IElementType outerElementType)
    {
        super(debugName, language, templateElementType, outerElementType);
    }

    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider viewProvider)
    {
        return getLanguage();
    }

    @NotNull
    protected TemplateDataModifications collectTemplateModifications(@NotNull CharSequence sourceCode, @NotNull Lexer lexer)
    {
        TemplateDataModifications modifications = new TemplateDataModifications();
        lexer.start(sourceCode);

        while (lexer.getTokenType() != null)
        {
            if (lexer.getTokenType() == OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE)
            {
                /**
                 * The only place, where the javascript block is preceded by " is the variable declaration in macro parameter,
                 * typically the varName and indexName params in oxy.repeat (see {@link ool.intellij.plugin.lang.lexer.OxyTemplateLexer#decideParameterType}).
                 * These would be parsed as reference expressions and have to be replaced by var statements.
                 */
                if (lexer.getBufferSequence().charAt(lexer.getTokenStart() - 1) == '"')
                {
                    modifications.addRangeToRemove(lexer.getTokenStart(), "var ");
                }

                modifications.addRangeToRemove(lexer.getTokenEnd(), "\n");
            }
            else
            {
                TextRange range = TextRange.create(lexer.getTokenStart(), lexer.getTokenEnd());
                modifications.addOuterRange(range, false);
            }

            lexer.advance();
        }

        return modifications;
    }

}
