package ool.intellij.plugin.lang.lexer;

import static ool.intellij.plugin.psi.OxyTemplateTypes.*;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import com.intellij.psi.tree.IElementType;
import java.util.Stack;

%%

%public
%class AbstractOxyTemplateLexer
%function advance
%type IElementType
%abstract
%unicode

%{
    protected CharSequence lastSeenMacroName;

    protected CharSequence lastSeenAttributeName;

    private Stack<Integer> stack = new Stack();

    abstract protected IElementType decideParameterType();

    abstract protected CharSequence getTokenText();

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
        int newState = YYINITIAL;

        while(count-- != 0 && ! stack.isEmpty())
        {
            newState = stack.pop();
        }

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
        return trimElement(element, false);
    }

    private IElementType trimElement(IElementType element, boolean pushbackWhitespace)
    {
        CharSequence text = getTokenText();

        if(text.length() == 0)
        {
            return null;
        }

        int trailingWhitespaceCount = 0;

        for(int i = text.length() - 1; i >= 0 && Character.isWhitespace(text.charAt(i)); i--)
        {
            trailingWhitespaceCount++;
        }

        if(pushbackWhitespace)
        {
            yypushback(trailingWhitespaceCount);
        }

        if(text.length() > trailingWhitespaceCount)
        {
            return element;
        }

        return WHITE_SPACE;
    }

    private void pushbackEncodedEntity()
    {
        int tokenEnd = getTokenEnd();

        if(tokenEnd + 1 > zzBuffer.length())
        {
            return;
        }
        if(zzBuffer.charAt(tokenEnd) != ';')
        {
            return;
        }

        CharSequence text = getTokenText();

        for(int i = text.length() - 1; i >= 0; i--)
        {
            if(text.charAt(i) == '&')
            {
                yypushback(text.length() - i);
            }
        }
    }

%}

%state S_BLOCK
%state S_DIRECTIVE_BLOCK
%state S_JAVASCRIPT_BLOCK
%state S_JAVASCRIPT
%state S_DIRECTIVE_PARAM
%state S_MACRO_TAG_START
%state S_MACRO_OPEN_OR_EMPTY_TAG
%state S_MACRO_CLOSE_TAG
%state S_MACRO_PARAM_ASIGNMENT
%state S_MACRO_NAME
%state S_MACRO_PARAM_DQD
%state S_MACRO_PARAM_SQD
%state S_MACRO_PARAM_EXPRESSION_DQD
%state S_MACRO_PARAM_EXPRESSION_SQD
%state S_COMMENT_BLOCK

OPEN_BLOCK_MARKER = <%
OPEN_BLOCK_MARKER_PRINT = <%=
CLOSE_BLOCK_MARKER = %>
OPEN_BLOCK_MARKER_DIRECTIVE = <%@
BLOCK_COMMENT_START = <\/\/
BLOCK_COMMENT_END = \/\/>
HTML_BLOCK = !([^]*({OPEN_BLOCK_MARKER}|{BLOCK_COMMENT_START}|{XML_TAG_START}{MACRO_XML_PREFIX}|{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX})[^]*)

MACRO_NAME = [A-Za-z][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)*(\[[A-Za-z][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)*\])*
MACRO_XML_NAMESPACE = m
MACRO_XML_PREFIX = {MACRO_XML_NAMESPACE}{XML_NAMESPACE_DELIMITER}
MACRO_PARAM_EXPRESSION_STATEMENT_START = expr:
XML_ENCODED_ENTITY = &#?[a-z0-9]+;
XML_NAMESPACE_DELIMITER = :
XML_TAG_START = <
XML_CLOSE_TAG_START = <\/
XML_TAG_END = >
XML_EMPTY_TAG_END = \/>

LINE_TERMINATOR = \r|\n|\r\n
CHARS = [^ \t\f\r\n\r\n\"]
WHITE_CHARS = [\t \f]
WHITE_SPACE = {LINE_TERMINATOR}|{WHITE_CHARS}
SPECIAL_CHARS = [<|>|%|\"|\\|@|:|\/|=|\.\-]
NON_SPECIAL_CHARS = !([^]*({SPECIAL_CHARS}|{WHITE_SPACE})[^]*)

%%

// html block
<YYINITIAL>
{
    // ...<%
    {HTML_BLOCK}{OPEN_BLOCK_MARKER}
    {
        IElementType el;

        yypushback(2);
        yypushstate(S_BLOCK);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...<m:
    {HTML_BLOCK}{XML_TAG_START}{MACRO_XML_PREFIX}
    {
        IElementType el;

        yypushback(3);
        yypushstate(S_MACRO_TAG_START);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...</m:
    {HTML_BLOCK}{XML_CLOSE_TAG_START}{MACRO_XML_PREFIX}
    {
        IElementType el;

        yypushback(4);
        yypushstate(S_MACRO_TAG_START);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    // ...<//
    {HTML_BLOCK}{BLOCK_COMMENT_START}
    {
        IElementType el;

        yypushback(3);
        yypushstate(S_COMMENT_BLOCK);

        if((el = trimElement(T_TEMPLATE_HTML_CODE)) != null) return el;
    }
    {HTML_BLOCK}
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

<S_JAVASCRIPT_BLOCK>
{
    {CLOSE_BLOCK_MARKER}
    {
        yypushback(2);
        yypopstate();
    }
    {WHITE_SPACE}+
    {
        yypushstate(S_JAVASCRIPT);
        return WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypushstate(S_JAVASCRIPT);
    }
}

<S_JAVASCRIPT>
{
    !([^]*({CLOSE_BLOCK_MARKER})[^]*){CLOSE_BLOCK_MARKER}
    {
        IElementType el;

        yypushback(2);
        yypopstate();

        if((el = trimElement(T_TEMPLATE_JAVASCRIPT_CODE, true)) != null) return el;
    }
    !([^]*({CLOSE_BLOCK_MARKER})[^]*)
    {
        return T_TEMPLATE_JAVASCRIPT_CODE;
        // followed by eof
    }
}
// ... //>
<S_COMMENT_BLOCK>
{
    !([^]*({BLOCK_COMMENT_END})[^]*){BLOCK_COMMENT_END}
    {
        yypopstate();
        return T_BLOCK_COMMENT;
    }
    !([^]*({BLOCK_COMMENT_END})[^]*)
    {
        return T_BLOCK_COMMENT;
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
        yypushstate(S_MACRO_OPEN_OR_EMPTY_TAG);
        return T_XML_TAG_START;
    }
    {XML_CLOSE_TAG_START}
    {
        yypushstate(S_MACRO_CLOSE_TAG);
        return T_XML_CLOSE_TAG_START;
    }
}
// m:foo.bar param_name="[expr:]param" [/]>
<S_MACRO_OPEN_OR_EMPTY_TAG>
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
        lastSeenAttributeName = getTokenText();
        return T_MACRO_PARAM_NAME;
    }
    {XML_TAG_END}
    {
        lastSeenAttributeName = null;
        yypopstate(2);
        return T_XML_OPEN_TAG_END;
    }
    {XML_EMPTY_TAG_END}
    {
        lastSeenAttributeName = null;
        yypopstate(2);
        return T_XML_EMPTY_TAG_END;
    }
    {WHITE_SPACE}+
    {
        return WHITE_SPACE;
    }
    .
    {
        lastSeenAttributeName = null;
        yypushback(1);
        yypopstate(2);
    }
}

<S_MACRO_NAME>
{
    {MACRO_NAME}
    {
        yypopstate();
        lastSeenMacroName = getTokenText();
        return T_MACRO_NAME;
    }
    .
    {
        yypushback(1);
        yypopstate();
    }
}
// =', ="
<S_MACRO_PARAM_ASIGNMENT>
{
    \=
    {
        return T_MACRO_PARAM_ASSIGNMENT;
    }
    \"
    {
        yypushstate(S_MACRO_PARAM_DQD);
        return T_MACRO_PARAM_BOUNDARY;
    }
    \'
    {
        yypushstate(S_MACRO_PARAM_SQD);
        return T_MACRO_PARAM_BOUNDARY;
    }
    {WHITE_SPACE}+
    {
        return WHITE_SPACE;
    }
    .
    {
        yypopstate();
        yypushback(1);
    }
}
// ...", expr:
<S_MACRO_PARAM_DQD>
{
    {XML_ENCODED_ENTITY}
    {
        return T_XML_ENCODED_ENTITY;
    }
    !([^]*({MACRO_PARAM_EXPRESSION_STATEMENT_START}|{XML_ENCODED_ENTITY}|\")[^]*)
    {
        pushbackEncodedEntity();
        return decideParameterType();
    }
    {MACRO_PARAM_EXPRESSION_STATEMENT_START}
    {
        yypushstate(S_MACRO_PARAM_EXPRESSION_DQD);
        return T_MACRO_PARAM_EXPRESSION_STATEMENT;
    }
    \"
    {
        yypopstate(2);
        return T_MACRO_PARAM_BOUNDARY;
    }
}
// ...', expr:
<S_MACRO_PARAM_SQD>
{
    {XML_ENCODED_ENTITY}
    {
        return T_XML_ENCODED_ENTITY;
    }
    !([^]*({MACRO_PARAM_EXPRESSION_STATEMENT_START}|{XML_ENCODED_ENTITY}|\')[^]*)
    {
        pushbackEncodedEntity();
        return decideParameterType();
    }
    {MACRO_PARAM_EXPRESSION_STATEMENT_START}
    {
        yypushstate(S_MACRO_PARAM_EXPRESSION_SQD);
        return T_MACRO_PARAM_EXPRESSION_STATEMENT;
    }
    \'
    {
        yypopstate(2);
        return T_MACRO_PARAM_BOUNDARY;
    }
}
// ..."
<S_MACRO_PARAM_EXPRESSION_DQD>
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
// ...'
<S_MACRO_PARAM_EXPRESSION_SQD>
{
    !([^]*(\')[^]*)
    {
        return trimElement(T_TEMPLATE_JAVASCRIPT_CODE);
    }
    \'
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
        return WHITE_SPACE;
    }
    .
    {
        yypushback(1);
        yypopstate(2);
    }
}

{WHITE_SPACE}+
{
    return WHITE_SPACE;
}
.
{
    return BAD_CHARACTER;
}
