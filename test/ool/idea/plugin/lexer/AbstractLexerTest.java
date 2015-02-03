package ool.idea.plugin.lexer;

import com.intellij.psi.tree.IElementType;
import java.io.IOException;
import ool.idea.plugin.lang.lexer.OxyTemplateLexerAdapter;
import org.junit.Before;

/**
 * 12/16/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractLexerTest
{
    protected OxyTemplateLexerAdapter lexer;

    @Before
    public void initLexer()
    {
        lexer = new OxyTemplateLexerAdapter();
    }

    protected IElementType nextToken() throws IOException
    {
        return lexer.getFlex().advance();
    }

}
