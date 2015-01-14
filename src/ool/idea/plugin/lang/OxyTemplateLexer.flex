package ool.idea.plugin.lang;

import com.intellij.lexer.*;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import static ool.idea.plugin.psi.OxyTemplateTypes.*;
import com.intellij.util.containers.Stack;

%%

%{
    private Stack<Integer> stack = new Stack();

    public OxyTemplateLexer()
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

    private void yypopstate(int count)
    {
        int newState = 0;
        while(count-- != 0 && ! stack.isEmpty()) newState = stack.pop();

        yybegin(newState);
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
%class OxyTemplateLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state S_BLOCK
%state S_DIRECTIVE_BLOCK
%state S_JAVASCRIPT_BLOCK
%state S_DIRECTIVE_PARAM
%state S_MACRO_TAG_START
%state S_MACRO_OPEN_OR_UNPAIRED_TAG
%state S_MACRO_CLOSE_TAG
%state S_MACRO_PARAM_ASIGNMENT
%state S_MACRO_NAME
%state S_MACRO_PARAM
%state S_MACRO_PARAM_EXPRESSION

OPEN_BLOCK_MARKER = <%
OPEN_BLOCK_MARKER_PRINT = <%=
CLOSE_BLOCK_MARKER = %>

OPEN_BLOCK_MARKER_DIRECTIVE = <%@

MACRO_NAME = [A-Za-z][A-Za-z0-9_]*(\.[A-Za-z][A-Za-z0-9_]*)*\.{0,1}
MACRO_XML_NAMESPACE = m
MACRO_XML_PREFIX = {MACRO_XML_NAMESPACE}{XML_NAMESPACE_DELIMITER}
MACRO_PARAM_EXPRESSION_STATEMENT_START = expr:
XML_NAMESPACE_DELIMITER = :
XML_TAG_START = <
XML_CLOSE_TAG_START = <\/
XML_TAG_END = >
XML_UNPAIRED_TAG_END = \/>

LINE_TERMINATOR = \r|\n|\r\n
CHARS = [^ \t\f\r\n\r\n\"]
WHITE_CHARS = [\t \f]
WHITE_SPACE = {LINE_TERMINATOR} | {WHITE_CHARS}
SPECIAL_CHARS = [<|>|%|\"|\\|@|:|\/|=|\.\-]
NON_SPECIAL_CHARS = !([^]*({SPECIAL_CHARS}|{WHITE_SPACE})[^]*)

%%

// html block
<YYINITIAL>
{
    // ...<%
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){OPEN_BLOCK_MARKER}
    {
        IElementType el;

        yypushback(2);
        yypushstate(S_BLOCK);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...<m:
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){XML_TAG_START}{MACRO_XML_PREFIX}
    {
        IElementType el;

        yypushback(3);
        yypushstate(S_MACRO_TAG_START);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...</m:
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*){XML_CLOSE_TAG_START}{MACRO_XML_PREFIX}
    {
        IElementType el;

        yypushback(4);
        yypushstate(S_MACRO_TAG_START);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    !([^]*({OPEN_BLOCK_MARKER}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*)
    {
        return trimElement(T_TEMPLATE_HTML_CODE);
    }
}
// <%, %>, <%=, <%@
<S_BLOCK>
{
    {OPEN_BLOCK_MARKER_DIRECTIVE}
    {
        yypushstate(S_DIRECTIVE_BLOCK);
        return T_OPEN_BLOCK_MARKER_DIRECTIVE;
    }
    {OPEN_BLOCK_MARKER}
    {
        yypushstate(S_JAVASCRIPT_BLOCK);
        return T_OPEN_BLOCK_MARKER;
    }
    {OPEN_BLOCK_MARKER_PRINT}
    {
        yypushstate(S_JAVASCRIPT_BLOCK);
        return T_OPEN_BLOCK_MARKER_PRINT;
    }
    {CLOSE_BLOCK_MARKER}
    {
        yypopstate();
        return T_CLOSE_BLOCK_MARKER;
    }
}
// javascript block
<S_JAVASCRIPT_BLOCK>
{
    !([^]*({CLOSE_BLOCK_MARKER})[^]*){CLOSE_BLOCK_MARKER}
    {
        IElementType el;

        yypushback(2);
        yypopstate();

        if((el = trimElement(T_TEMPLATE_JAVASCRIPT_CODE)) != null) return el;
    }
    !([^]*({CLOSE_BLOCK_MARKER})[^]*)
    {
        return trimElement(T_TEMPLATE_JAVASCRIPT_CODE);
    }
}
// directive "paramX" "paramY"
<S_DIRECTIVE_BLOCK>
{
    !([^]*({WHITE_SPACE}|{SPECIAL_CHARS})[^]*)
    {
        return T_DIRECTIVE;
    }
    \"
    {
        yypushstate(S_DIRECTIVE_PARAM);
        return T_DIRECTIVE_PARAM_BOUNDARY;
    }
    {CLOSE_BLOCK_MARKER}
    {
        yypushback(2);
        yypopstate();
    }
    {CHARS}
    {
        yypushback(1);
        yyresetstate();
    }
}

<S_DIRECTIVE_PARAM>
{
    !([^]*(\"|{LINE_TERMINATOR})[^]*)
    {
        return T_DIRECTIVE_PARAM;
    }
    \"
    {
        yypopstate();
        return T_DIRECTIVE_PARAM_BOUNDARY;
    }
    {LINE_TERMINATOR}
    {
        yypushback(1);
        yyresetstate();
    }
}
// <, </
<S_MACRO_TAG_START>
{
    {XML_TAG_START}
    {
        yypushstate(S_MACRO_OPEN_OR_UNPAIRED_TAG);
        return T_XML_TAG_START;
    }
    {XML_CLOSE_TAG_START}
    {
        yypushstate(S_MACRO_CLOSE_TAG);
        return T_XML_CLOSE_TAG_START;
    }
}
// m:foo.bar param_name="[expr:]param" [/]>
<S_MACRO_OPEN_OR_UNPAIRED_TAG>
{
    {MACRO_XML_NAMESPACE}
    {
        return T_MACRO_XML_NAMESPACE;
    }
    {XML_NAMESPACE_DELIMITER}
    {
        yypushstate(S_MACRO_NAME);
        return T_XML_NAMESPACE_DELIMITER;
    }
    {NON_SPECIAL_CHARS}+
    {
        yypushstate(S_MACRO_PARAM_ASIGNMENT);
        return T_MACRO_PARAM_NAME;
    }
    {XML_TAG_END}
    {
        yypopstate(2);
        return T_XML_OPEN_TAG_END;
    }
    {XML_UNPAIRED_TAG_END}
    {
        yypopstate(2);
        return T_XML_UNPAIRED_TAG_END;
    }
    {WHITE_SPACE}+
    {
        return TokenType.WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypopstate(2);
    }
}

<S_MACRO_NAME>
{
    {MACRO_NAME}
    {
        yypopstate();
        return T_MACRO_NAME;
    }
    .
    {
        yypushback(1);
        yypopstate();
    }
}

<S_MACRO_PARAM_ASIGNMENT>
{
    \=
    {
        return T_MACRO_PARAM_ASSIGNMENT;
    }
    \"
    {
        yypushstate(S_MACRO_PARAM);
        return T_MACRO_PARAM_BOUNDARY;
    }
    {WHITE_SPACE}+
    {
        return TokenType.WHITE_SPACE;
    }
    .
    {
        yypopstate();
        yypushback(1);
    }
}

<S_MACRO_PARAM>
{
    !([^]*({MACRO_PARAM_EXPRESSION_STATEMENT_START}|\")[^]*)
    {
        return T_MACRO_PARAM;
    }
    {MACRO_PARAM_EXPRESSION_STATEMENT_START}
    {
        yypushstate(S_MACRO_PARAM_EXPRESSION);
        return T_MACRO_PARAM_EXPRESSION_STATEMENT;
    }
    \"
    {
        yypopstate(2);
        return T_MACRO_PARAM_BOUNDARY;
    }
}

<S_MACRO_PARAM_EXPRESSION>
{
    !([^]*(\")[^]*)
    {
        return trimElement(T_TEMPLATE_JAVASCRIPT_CODE);
    }
    \"
    {
        yypopstate(3);
        return T_MACRO_PARAM_BOUNDARY;
    }
}
// m:foo.bar>
<S_MACRO_CLOSE_TAG>
{
    {MACRO_XML_NAMESPACE}
    {
        return T_MACRO_XML_NAMESPACE;
    }
    {XML_NAMESPACE_DELIMITER}
    {
        yypushstate(S_MACRO_NAME);
        return T_XML_NAMESPACE_DELIMITER;
    }
    {XML_TAG_END}
    {
        yypopstate(2);
        return T_XML_CLOSE_TAG_END;
    }
    {WHITE_SPACE}+
    {
        return TokenType.WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypopstate(2);
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
