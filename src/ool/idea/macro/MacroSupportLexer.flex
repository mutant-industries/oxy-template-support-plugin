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
        // dummy
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

%state BLOCK
%state DIRECTIVE_BLOCK
%state JAVASCRIPT_BLOCK
%state DIRECTIVE_PARAMETER

OPEN_BLOCK_MARKER_PRINT = <%=
OPEN_BLOCK_MARKER_DIRECTIVE = <%@
OPEN_BLOCK_MARKER = <%
CLOSE_BLOCK_MARKER = %>
DIRECTIVE_PARAM_BOUNDARY = \"

LINE_TERMINATOR = \r|\n|\r\n
WHITE_SPACE     = {LINE_TERMINATOR} | [ \t\f]
SPECIAL_CHARS   = [<>%\"\\@:/=\.]

%%

<YYINITIAL> {
    !([^]*({OPEN_BLOCK_MARKER})[^]*){OPEN_BLOCK_MARKER} {
        yypushback(2);
        yypushstate(BLOCK);

        if(yytext().toString().trim().length() == 0) {
            return TokenType.WHITE_SPACE;
        }
        return TEMPLATE_HTML_CODE;
    }
    !([^]*({OPEN_BLOCK_MARKER})[^]*) {

        if(yytext().toString().trim().length() == 0) {
            return TokenType.WHITE_SPACE;
        }

        return TEMPLATE_HTML_CODE;
    }
    {OPEN_BLOCK_MARKER} {
        yypushback(2);
        yypushstate(BLOCK);
    }
}
<BLOCK> {
    {OPEN_BLOCK_MARKER_DIRECTIVE} {
        yypushstate(DIRECTIVE_BLOCK);
        return OPEN_BLOCK_MARKER_DIRECTIVE;
    }
    {OPEN_BLOCK_MARKER} {
        yypushstate(JAVASCRIPT_BLOCK);
        return OPEN_BLOCK_MARKER;
    }
    {OPEN_BLOCK_MARKER_PRINT} {
        yypushstate(JAVASCRIPT_BLOCK);
        return OPEN_BLOCK_MARKER_PRINT;
    }
    {CLOSE_BLOCK_MARKER} {
        yypopstate();
        return CLOSE_BLOCK_MARKER;
    }
}
<JAVASCRIPT_BLOCK> {

    !([^]*({CLOSE_BLOCK_MARKER})[^]*){CLOSE_BLOCK_MARKER} {

        yypushback(2);
        yypopstate();

        if(yytext().toString().trim().length() == 0) {
            return TokenType.WHITE_SPACE;
        }

        return TEMPLATE_JAVASCRIPT_CODE;
    }
    !([^]*({CLOSE_BLOCK_MARKER})[^]*) {

        if(yytext().toString().trim().length() == 0) {
            return TokenType.WHITE_SPACE;
        }

        return TEMPLATE_JAVASCRIPT_CODE;
    }
    {CLOSE_BLOCK_MARKER} {
        yypopstate();
    }
}
<DIRECTIVE_BLOCK> {
    !([^]*({WHITE_SPACE}|{SPECIAL_CHARS})[^]*) {
        return DIRECTIVE;
    }
    {DIRECTIVE_PARAM_BOUNDARY} {
        yypushstate(DIRECTIVE_PARAMETER);
        return DIRECTIVE_PARAM_BOUNDARY;
    }
    {CLOSE_BLOCK_MARKER} {
        yypushback(2);
        yypopstate();
    }
}
<DIRECTIVE_PARAMETER> {
    !([^]*({CLOSE_BLOCK_MARKER}|{DIRECTIVE_PARAM_BOUNDARY}|{WHITE_SPACE})[^]*) {
        return DIRECTIVE_PARAM;
    }
    {DIRECTIVE_PARAM_BOUNDARY} {
        yypopstate();
        return DIRECTIVE_PARAM_BOUNDARY;
    }
    {CLOSE_BLOCK_MARKER} {
        yypushback(2);
        yypopstate();
    }
    {WHITE_SPACE}+ {
        return TokenType.BAD_CHARACTER;
    }
}

{WHITE_SPACE}+ {
    return TokenType.WHITE_SPACE;
}
. {
    return TokenType.BAD_CHARACTER;
}
