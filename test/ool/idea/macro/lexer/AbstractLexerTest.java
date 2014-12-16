package ool.idea.macro.lexer;

import com.intellij.psi.tree.IElementType;
import java.io.IOException;
import ool.idea.macro.MacroSupportLexerAdapter;
import org.junit.Before;

/**
 * 12/16/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractLexerTest
{
    protected MacroSupportLexerAdapter lexer;

    @Before
    public void initLexer()
    {
        lexer = new MacroSupportLexerAdapter();
    }

    protected IElementType nextToken() throws IOException
    {
        return lexer.getFlex().advance();
    }

}
