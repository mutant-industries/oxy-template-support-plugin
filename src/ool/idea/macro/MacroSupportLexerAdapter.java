package ool.idea.macro;

import com.intellij.lexer.FlexAdapter;

/**
 * 12/12/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportLexerAdapter extends FlexAdapter
{
    public MacroSupportLexerAdapter()
    {
        super(new MacroSupportLexer());
    }

}
