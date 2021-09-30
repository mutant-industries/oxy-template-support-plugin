package ool.intellij.plugin.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * 3/12/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ChildElementFinder extends PsiRecursiveElementVisitor
{
    private PsiElement result;

    private final TokenSet searchedElements;

    private TextRange allowedRange;

    public ChildElementFinder(IElementType... searchedElements)
    {
        this.searchedElements = TokenSet.create(searchedElements);
    }

    public void setAllowedRange(TextRange allowedRange)
    {
        this.allowedRange = allowedRange;
    }

    @Override
    public void visitElement(PsiElement element)
    {
        if (searchedElements.contains(element.getNode().getElementType()) && (allowedRange == null
                || allowedRange.contains(element.getTextRange())))
        {
            result = element;
        }
        else
        {
            super.visitElement(element);
        }
    }

    public PsiElement getResult()
    {
        return result;
    }

}
