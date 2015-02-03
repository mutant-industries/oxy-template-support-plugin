package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableDefinitionReference implements PsiReference
{
    private final PsiLiteralExpression literalExpression;

    public GlobalVariableDefinitionReference(@NotNull PsiLiteralExpression literalExpression)
    {
        this.literalExpression = literalExpression;
    }

    @Override
    public PsiElement getElement()
    {
        return literalExpression;
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(1, literalExpression.getTextLength() - 1);
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return new GlobalVariableDefinition(literalExpression);
    }

    @NotNull
    @Override
    public String getCanonicalText()
    {
        return (String) literalExpression.getValue();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
    {
        return null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("bindToElement not implemented");
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return PsiReference.EMPTY_ARRAY;
    }

    @Override
    public boolean isSoft()
    {
        return false;
    }

}
