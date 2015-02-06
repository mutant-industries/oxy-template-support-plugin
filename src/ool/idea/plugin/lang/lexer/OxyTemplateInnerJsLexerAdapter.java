package ool.idea.plugin.lang.lexer;

import com.intellij.lang.javascript.JSFlexAdapter;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
* 2/2/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class OxyTemplateInnerJsLexerAdapter extends LayeredLookaheadLexer
{
    public OxyTemplateInnerJsLexerAdapter()
    {
        this(new JSFlexAdapter(JavascriptLanguage.DIALECT_OPTION_HOLDER));
    }

    public OxyTemplateInnerJsLexerAdapter(@NotNull JSFlexAdapter jsFlexAdapter)
    {
        super(jsFlexAdapter, new RhinoDialectSpecificLexerAdapter(),
                TokenSet.create(JSTokenTypes.FOR_KEYWORD));
    }

}
