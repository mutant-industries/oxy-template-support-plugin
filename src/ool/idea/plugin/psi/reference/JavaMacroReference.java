package ool.idea.plugin.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.MacroNameIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroReference extends MacroReference<PsiIdentifier>
{
    public JavaMacroReference(@NotNull MacroNameIdentifier macroNameIdentifier, @NotNull PsiIdentifier referencedElement)
    {
        super(macroNameIdentifier, referencedElement);
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return referencedElement;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("handleElementRename not implemented yet");
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("bindToElement not implemented yet");
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return element instanceof PsiIdentifier && referencedElement.isEquivalentTo(element);
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return new Object[0];
    }

}
