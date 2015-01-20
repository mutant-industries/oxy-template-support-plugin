package ool.idea.plugin.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroReference<T extends PsiElement> implements PsiReference
{
    protected final T referencedIdentifier;

    public MacroReference(@NotNull T referencedIdentifier)
    {
        this.referencedIdentifier = referencedIdentifier;
    }

    @Override
    public PsiElement getElement()
    {
        return referencedIdentifier;
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(0, referencedIdentifier.getText().length());
    }

    @NotNull
    @Override
    public String getCanonicalText()
    {
        return referencedIdentifier.getText();
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
