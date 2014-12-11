package ool.idea.macro;
import com.intellij.lexer.*;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import static ool.idea.macro.psi.MacroSupportTypes.*;
import com.intellij.util.containers.Stack;

%%

%{
    private Stack<Integer> stack = new Stack<Integer>();

    public MacroSupportLexer() {
        this((java.io.Reader)null);
    }

    public void yypushstate(int newState) {
        stack.push(yystate());
        yybegin(newState);
    }

    public void yypopstate() {
        yybegin(stack.pop());
    }

%}

%public
%class MacroSupportLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state MACRO_CONTENT

MACRO_OPEN_TAG_EQ = <%=
MACRO_OPEN_TAG = <%
MACRO_CLOSE_TAG = %>

%%
<YYINITIAL> {
    !([^]*({MACRO_OPEN_TAG})[^]*) {MACRO_OPEN_TAG}  {
            yypushback(2);
            yypushstate(MACRO_CONTENT);
            return TEMPLATE_HTML_TEXT;
       }
    !([^]*({MACRO_OPEN_TAG})[^]*) { return TEMPLATE_HTML_TEXT; }
}
<MACRO_CONTENT> {
    {MACRO_OPEN_TAG_EQ}            { return T_MACRO_OPEN_TAG_EQ; }
    {MACRO_OPEN_TAG}            { return T_MACRO_OPEN_TAG; }
    {MACRO_CLOSE_TAG}             { yypopstate(); return T_MACRO_CLOSE_TAG; }
    !([^]*({MACRO_OPEN_TAG})[^]*)%>  { yypushback(2); return TEMPLATE_JAVASCRIPT_TEXT; }
}

//{WHITE_SPACE}                      { return TokenType.WHITE_SPACE; }
.                                        { return BAD_CHARACTER; }
