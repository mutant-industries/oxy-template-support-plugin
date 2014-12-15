package ool.idea.macro;
import com.intellij.lexer.*;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import static ool.idea.macro.psi.MacroSupportTypes.*;
import com.intellij.util.containers.Stack;

%%

%{
    private Stack<Integer> stack = new Stack();

    public MacroSupportLexer()
    {
        // dummy
    }

    private void yypushstate(int newState)
    {
        stack.push(yystate());
        yybegin(newState);
    }

    private void yypopstate()
    {
        yybegin(stack.pop());
    }

    private void yyresetstate()
    {
        while( ! stack.isEmpty())
        {
            yybegin(stack.pop());
        }
    }

    private IElementType trimElement(IElementType element)
    {
        String text = yytext().toString();

        if(text.trim().length() > 0) return element;
        if(text.length() > 0) return TokenType.WHITE_SPACE;

        return null;
    }
%}

%public
%class MacroSupportLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state S_BLOCK
%state S_DIRECTIVE_BLOCK
%state S_JAVASCRIPT_BLOCK
%state S_DIRECTIVE_PARAMETER
%state S_MACRO_OPEN_OR_UNPAIRED_TAG
%state S_MACRO_CLOSE_TAG
%state S_MACRO_PARAM_ASIGNMENT
%state S_MACRO_NAME
%state S_MACRO_PARAMETER
%state S_MACRO_PARAMETER_EXPRESSION

OPEN_BLOCK_MARKER = <%
OPEN_BLOCK_MARKER_PRINT = <%=
CLOSE_BLOCK_MARKER = %>

OPEN_BLOCK_MARKER_DIRECTIVE = <%@

MACRO_NAME = [A-Za-z][A-Za-z0-9_]+(\.[A-Za-z][A-Za-z0-9_]+)*\.{0,1}
MACRO_XML_PREFIX = m:
MACRO_PARAM_EXPRESSION_STATEMENT_START = expr:
XML_TAG_START = <
XML_CLOSE_TAG_START = <\/
XML_TAG_END = >
XML_UNPAIRED_TAG_END = \/>

LINE_TERMINATOR = \r|\n|\r\n
CHARS = [^ \t\f\r\n\r\n\"]
WHITE_CHARS = [\t \f]
WHITE_SPACE = {LINE_TERMINATOR} | {WHITE_CHARS}
SPECIAL_CHARS = [<|>|%|\"|\\|@|:|\/|=|\.]+
NON_SPECIAL_CHARS = !([^]*({SPECIAL_CHARS}|{WHITE_SPACE})[^]*)

%%

// html block
<YYINITIAL>
{
    // ...<%
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){OPEN_BLOCK_MARKER}
    {
        final IElementType el;

        yypushback(2);
        yypushstate(S_BLOCK);

        if((el = trimElement(TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...<m:
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){XML_TAG_START}{MACRO_XML_PREFIX}
    {
        final IElementType el;

        yypushback(3);
        yypushstate(S_MACRO_OPEN_OR_UNPAIRED_TAG);

        if((el = trimElement(TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...</m:
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){XML_CLOSE_TAG_START}{MACRO_XML_PREFIX}
    {
        final IElementType el;

        yypushback(4);
        yypushstate(S_MACRO_CLOSE_TAG);

        if((el = trimElement(TEMPLATE_HTML_CODE)) != null) return el;
    }
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*)
    {
        return trimElement(TEMPLATE_HTML_CODE);
    }
}
// <%, %>, <%=, <%@
<S_BLOCK>
{
    {OPEN_BLOCK_MARKER_DIRECTIVE}
    {
        yypushstate(S_DIRECTIVE_BLOCK);
        return OPEN_BLOCK_MARKER_DIRECTIVE;
    }
    {OPEN_BLOCK_MARKER}
    {
        yypushstate(S_JAVASCRIPT_BLOCK);
        return OPEN_BLOCK_MARKER;
    }
    {OPEN_BLOCK_MARKER_PRINT}
    {
        yypushstate(S_JAVASCRIPT_BLOCK);
        return OPEN_BLOCK_MARKER_PRINT;
    }
    {CLOSE_BLOCK_MARKER}
    {
        yypopstate();
        return CLOSE_BLOCK_MARKER;
    }
}
// javascript block
<S_JAVASCRIPT_BLOCK>
{
    !([^]*({CLOSE_BLOCK_MARKER})[^]*){CLOSE_BLOCK_MARKER}
    {
        final IElementType el;

        yypushback(2);
        yypopstate();

        if((el = trimElement(TEMPLATE_JAVASCRIPT_CODE)) != null) return el;
    }
    !([^]*({CLOSE_BLOCK_MARKER})[^]*)
    {
        if(yytext().toString().trim().length() == 0) return TokenType.WHITE_SPACE;

        return TEMPLATE_JAVASCRIPT_CODE;
    }
}
// <%@ directive "paramX" "paramY" %>
<S_DIRECTIVE_BLOCK>
{
    !([^]*({WHITE_SPACE}|{SPECIAL_CHARS})[^]*)
    {
        return DIRECTIVE;
    }
    \"
    {
        yypushstate(S_DIRECTIVE_PARAMETER);
        return DIRECTIVE_PARAM_BOUNDARY;
    }
    {CLOSE_BLOCK_MARKER}
    {
        yypushback(2);
        yypopstate();
    }
    {CHARS}|{LINE_TERMINATOR}
    {
        yyresetstate();
        return TokenType.BAD_CHARACTER;
    }
}

<S_DIRECTIVE_PARAMETER>
{
    !([^]*(\"|{LINE_TERMINATOR})[^]*)
    {
        return DIRECTIVE_PARAM;
    }
    \"
    {
        yypopstate();
        return DIRECTIVE_PARAM_BOUNDARY;
    }
    {LINE_TERMINATOR}
    {
        yyresetstate();
        return TokenType.BAD_CHARACTER;
    }
}
// <m:foo.bar param_name="[expr:]param" [/]>
<S_MACRO_OPEN_OR_UNPAIRED_TAG>
{
    {XML_TAG_START}
    {
        return XML_TAG_START;
    }
    {MACRO_XML_PREFIX}
    {
        yypushstate(S_MACRO_NAME);
        return MACRO_XML_PREFIX;
    }
    {NON_SPECIAL_CHARS}+
    {
        yypushstate(S_MACRO_PARAM_ASIGNMENT);
        return MACRO_PARAM_NAME;
    }
    {XML_TAG_END}
    {
        yypopstate();
        return XML_TAG_END;
    }
    {XML_UNPAIRED_TAG_END}
    {
        yypopstate();
        return XML_UNPAIRED_TAG_END;
    }
    {WHITE_SPACE}+
    {
        return TokenType.WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypopstate();
        return TokenType.BAD_CHARACTER;
    }
}

<S_MACRO_NAME>
{
    {MACRO_NAME}
    {
        yypopstate();
        return MACRO_NAME;
    }
    .
    {
        yypushback(1);
        yypopstate();
        return TokenType.BAD_CHARACTER;
    }
}

<S_MACRO_PARAM_ASIGNMENT>
{
    \=
    {
        return MACRO_PARAM_ASSIGNMENT;
    }
    \"
    {
        yypushstate(S_MACRO_PARAMETER);
        return MACRO_PARAM_BOUNDARY;
    }
    .
    {
        yyresetstate();
        return TokenType.BAD_CHARACTER;
    }
}

<S_MACRO_PARAMETER>
{
    !([^]*({MACRO_PARAM_EXPRESSION_STATEMENT_START}|\")[^]*)
    {
        return MACRO_PARAM;
    }
    {MACRO_PARAM_EXPRESSION_STATEMENT_START}
    {
        yypushstate(S_MACRO_PARAMETER_EXPRESSION);
        return MACRO_PARAM_EXPRESSION_STATEMENT;
    }
    \"
    {
        yypopstate(); yypopstate();
        return MACRO_PARAM_BOUNDARY;
    }
}

<S_MACRO_PARAMETER_EXPRESSION>
{
    !([^]*(\")[^]*)
    {
        return MACRO_PARAM_EXPRESSION;
    }
    \"
    {
        yypopstate(); yypopstate(); yypopstate();
        return MACRO_PARAM_BOUNDARY;
    }
}
//</m:foo.bar>
<S_MACRO_CLOSE_TAG>
{
    {XML_CLOSE_TAG_START}
    {
        return XML_CLOSE_TAG_START;
    }
    {MACRO_XML_PREFIX}
    {
        yypushstate(S_MACRO_NAME);
        return MACRO_XML_PREFIX;
    }
    {XML_TAG_END}
    {
        yypopstate();
        return XML_TAG_END;
    }
    {WHITE_SPACE}+
    {
        return TokenType.WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypopstate();
        return TokenType.BAD_CHARACTER;
    }
}

{WHITE_SPACE}+
{
    return TokenType.WHITE_SPACE;
}
.
{
    return TokenType.BAD_CHARACTER;
}
