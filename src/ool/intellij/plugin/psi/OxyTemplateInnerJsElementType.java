package ool.intellij.plugin.psi;

import java.util.LinkedList;
import java.util.Stack;

import ool.intellij.plugin.psi.visitor.LineBreaksFixingElementVisitor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;

/**
 * 2/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsElementType extends TemplateDataElementType
{
    private final ThreadLocal<Stack<LinkedList<Integer>>> offsets = new ThreadLocal<>();
    private final ThreadLocal<Stack<LinkedList<TextRange>>> variables = new ThreadLocal<>();

    public OxyTemplateInnerJsElementType(@NonNls String debugName, Language language, IElementType templateElementType,
                                         IElementType outerElementType)
    {
        super(debugName, language, templateElementType, outerElementType);
    }

    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider viewProvider)
    {
        return getLanguage();
    }

    @Override
    protected CharSequence createTemplateText(CharSequence buf, Lexer lexer)
    {
        if (offsets.get() == null || variables.get() == null)
        {
            offsets.set(new Stack<>());
            variables.set(new Stack<>());
        }

        offsets.get().push(new LinkedList<>());
        variables.get().push(new LinkedList<>());

        return super.createTemplateText(buf, lexer);
    }

    @Override
    protected void appendCurrentTemplateToken(StringBuilder result, CharSequence buf, Lexer lexer)
    {
        super.appendCurrentTemplateToken(result, buf, lexer);

        /**
         * The only place, where the javascript block is preceded by " is the variable declaration in macro parameter,
         * typically the varName and indexName params in oxy.repeat (see {@link ool.intellij.plugin.lang.lexer.OxyTemplateLexer#decideParameterType}).
         * These will be parsed as reference expressions, and have to be replaced by var statements (without var keyword actually)
         * in later stages ({@link #convertReferencesToVariables}).
         */
        if (lexer.getBufferSequence().charAt(lexer.getTokenStart() - 1) == '"')
        {
            variables.get().peek().add(TextRange.create(result.length() - lexer.getTokenEnd() + lexer.getTokenStart(), result.length()));
        }

        offsets.get().peek().add(result.length());

        result.append('\n');
    }

    @Override
    protected void prepareParsedTemplateFile(final FileElement root)
    {
        convertReferencesToVariables(root);
        root.acceptTree(new LineBreaksFixingElementVisitor(offsets.get().pop()));
    }

    private void convertReferencesToVariables(final FileElement root)
    {
        for (TextRange range : variables.get().pop())
        {
            LeafElement elementAt = root.findLeafElementAt(range.getStartOffset());
            CompositeElement parent;

            if (elementAt == null || elementAt.getElementType() != JSTokenTypes.IDENTIFIER
                    || elementAt.getTextLength() != range.getLength()
                    || (parent = elementAt.getTreeParent()).getElementType() != JSElementTypes.REFERENCE_EXPRESSION
                    || (parent = parent.getTreeParent()).getElementType() != JSElementTypes.EXPRESSION_STATEMENT)
            {
                continue;
            }

            ASTNode replacement = JSChangeUtil.createJSTreeFromText(root.getManager().getProject(), "var " + elementAt.getText());

            replacement.removeRange(replacement.getFirstChildNode(), replacement.getLastChildNode());

            parent.getTreeParent().replaceChild(parent, replacement);
        }
    }

}
