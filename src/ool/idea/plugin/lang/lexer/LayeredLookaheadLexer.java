package ool.idea.plugin.lang.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * 2/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class LayeredLookaheadLexer extends LookAheadLexer
{
    public final Lexer baseLexer;
    protected final Lexer lookaheadLexer;
    protected TokenSet switchTokens;

    public LayeredLookaheadLexer(@NotNull Lexer baseLexer, @NotNull Lexer lookaheadLexer, @NotNull TokenSet switchTokens)
    {
        super(baseLexer);

        this.baseLexer = baseLexer;
        this.lookaheadLexer = lookaheadLexer;
        this.switchTokens = switchTokens;
    }

    @Override
    protected void lookAhead(final Lexer baseLexer)
    {
        if(switchTokens.contains(baseLexer.getTokenType()))
        {
            lookaheadLexer.start(baseLexer.getBufferSequence(), baseLexer.getTokenStart(),
                    baseLexer.getBufferSequence().length());

            boolean subTokensFound = false;
            IElementType subToken;

            while((subToken = lookaheadLexer.getTokenType()) != null)
            {
                addToken(lookaheadLexer.getTokenEnd(), subToken);

                lookaheadLexer.advance();
                subTokensFound = true;
            }

            if(subTokensFound)
            {
                baseLexer.start(baseLexer.getBufferSequence(), lookaheadLexer.getTokenEnd(),
                        baseLexer.getBufferSequence().length(), baseLexer.getState());
            }
            else
            {
                addToken(baseLexer.getTokenType());
            }

            baseLexer.advance();
        }
        else
        {
            addToken(baseLexer.getTokenType());
            baseLexer.advance();
        }
    }

}
