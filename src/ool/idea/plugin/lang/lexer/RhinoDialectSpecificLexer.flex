package ool.idea.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.intellij.lang.javascript.JSTokenTypes.FOR_KEYWORD;

%%

%class RhinoDialectSpecificLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

FOREACH_KEYWORD = for\ each

%%

<YYINITIAL>
{
    {FOREACH_KEYWORD}
    {
        return FOR_KEYWORD;
    }
}

[^]
{
    yypushback(1);
    return null;
}
