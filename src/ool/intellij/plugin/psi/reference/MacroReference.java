package ool.intellij.plugin.psi.reference;

import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroReference implements PsiPolyVariantReference
{
    protected PsiElement referencingElement;

    private final int startOffset;

    private final int endOffset;

    public MacroReference(@NotNull PsiElement referencingElement, int startOffset, int endOffset)
    {
        this.referencingElement = referencingElement;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        ResolveResult[] multiResolve = multiResolve(false);

        if (multiResolve.length == 1)
        {
            return multiResolve[0].getElement();
        }
        else if (multiResolve.length > 1 && ! (referencingElement instanceof PsiLiteralExpression)
                && referencingElement.getTextLength() == endOffset)
        {
            ResolveResult result;

            if ((result = OxyTemplateHelper.multiResolveWithIncludeSearch(referencingElement, multiResolve)) != null)
            {
                return result.getElement();
            }
        }

        return null;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException
    {
        final ElementManipulator<PsiElement> manipulator = CachingReference.getManipulator(referencingElement);

        return referencingElement = manipulator.handleContentChange(getElement(), getRangeInElement(), newElementName);
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return element.isEquivalentTo(resolve());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode)
    {
        return ResolveCache.getInstance(referencingElement.getProject())
                .resolveWithCaching(this, MacroReferenceResolver.DEFAULT, false, false, referencingElement.getContainingFile());
    }

    @NotNull
    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(startOffset, endOffset);
    }

    @NotNull
    @Override
    public PsiElement getElement()
    {
        return referencingElement;
    }

    @NotNull
    @Override
    public String getCanonicalText()
    {
        return referencingElement.getText();
    }

    @Override
    public boolean isSoft()
    {
        return true;
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return EMPTY_ARRAY;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("bindToElement not implemented yet");
    }

}
