package ool.intellij.plugin.lexer;

import java.io.IOException;

import ool.intellij.plugin.lang.lexer.OxyTemplateLexerAdapter;

import com.intellij.psi.tree.IElementType;
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
