package ool.idea.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.RecursiveTreeElementVisitor;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import java.util.LinkedList;

/**
 * 2/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsElementType extends TemplateDataElementType
{
    private final LinkedList<Integer> offsets = new LinkedList<Integer>();
    private final LinkedList<TextRange> variables = new LinkedList<TextRange>();

    public OxyTemplateInnerJsElementType(String debugName, Language language, IElementType templateElementType, IElementType outerElementType)
    {
        super(debugName, language, templateElementType, outerElementType);
    }

    @Override
    protected CharSequence createTemplateText(CharSequence buf, Lexer lexer)
    {
        offsets.clear();
        variables.clear();

        return super.createTemplateText(buf, lexer);
    }

    @Override
    protected void appendCurrentTemplateToken(StringBuilder result, CharSequence buf, Lexer lexer)
    {
        super.appendCurrentTemplateToken(result, buf, lexer);

        if(lexer.getBufferSequence().charAt(lexer.getTokenStart() - 1) == '"')
        {
            variables.add(TextRange.create(result.length() - lexer.getTokenEnd() + lexer.getTokenStart(), result.length()));
        }

        offsets.add(result.length());
        result.append("\n");
    }

    @Override
    protected void prepareParsedTemplateFile(final FileElement root)
    {
        convertReferencesToVariables(root);
        fixLineBreaks(root);
    }

    @Override
    protected Language getTemplateFileLanguage(TemplateLanguageFileViewProvider viewProvider)
    {
        return getLanguage();
    }

    private void convertReferencesToVariables(final FileElement root)
    {
        for(TextRange range : variables)
        {
            LeafElement elementAt = root.findLeafElementAt(range.getStartOffset());
            CompositeElement parent;

            if(elementAt == null || elementAt.getElementType() != JSTokenTypes.IDENTIFIER
                    || elementAt.getTextLength() != range.getLength()
                    || (parent = elementAt.getTreeParent()).getElementType() != JSElementTypes.REFERENCE_EXPRESSION
                    || (parent = parent.getTreeParent()).getElementType() != JSElementTypes.EXPRESSION_STATEMENT)
            {
                continue;
            }

            ASTNode replacement = JSChangeUtil.createJSTreeFromText(root.getManager().getProject(), "var " + elementAt.getText());

            ASTNode varStatement = replacement.getFirstChildNode();
            ASTNode space = varStatement.getTreeNext();

            replacement.removeChild(varStatement);
            replacement.removeChild(space);

            parent.getTreeParent().replaceChild(parent, replacement);
        }

        variables.clear();
    }

    private void fixLineBreaks(final FileElement root)
    {
        root.acceptTree(new RecursiveTreeElementVisitor()
        {
            private int shift = 0;

            @Override
            protected boolean visitNode(TreeElement element)
            {
                return true;
            }

            @Override
            public void visitLeaf(LeafElement leaf)
            {
                if (offsets.isEmpty() || (shift + leaf.getTextOffset() + leaf.getTextLength() < offsets.peekFirst()))
                {
                    return;
                }

                while ( ! offsets.isEmpty() && offsets.peekFirst() < shift + leaf.getTextOffset())
                {
                    offsets.pollFirst();
                }

                StringBuilder newText = new StringBuilder(leaf.getText());
                int localShift = 0;

                while ( ! offsets.isEmpty() && offsets.peekFirst() < shift + leaf.getTextOffset() + leaf.getTextLength())
                {
                    int index = offsets.pollFirst() - (shift + localShift + leaf.getTextOffset());
                    newText.deleteCharAt(index);
                    localShift++;
                }

                shift += localShift;

                if (newText.length() > 0)
                {
                    TreeElement newAnchor = Factory.createSingleLeafElement(leaf.getElementType(), newText,
                            0, newText.length(), null, leaf.getManager());

                    leaf.rawInsertBeforeMe(newAnchor);
                }

                leaf.rawRemove();
            }
        });

        offsets.clear();
    }

}
