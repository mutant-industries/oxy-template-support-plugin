package ool.idea.plugin.lang.lexer;

import com.intellij.lexer.FlexAdapter;
import java.io.Reader;

/**
 * 2/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class RhinoDialectSpecificLexerAdapter extends FlexAdapter
{
    public RhinoDialectSpecificLexerAdapter()
    {
        super(new RhinoDialectSpecificLexer(((Reader)null)));
    }

}
