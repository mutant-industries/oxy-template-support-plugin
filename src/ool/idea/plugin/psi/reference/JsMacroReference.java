package ool.idea.plugin.psi.reference;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.MacroNameIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroReference extends MacroReference<JSElement>
{
    public JsMacroReference(@NotNull MacroNameIdentifier macroNameIdentifier, @NotNull JSElement referencedElement)
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
    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("bindToElement not implemented yet");
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return referencedElement.isEquivalentTo(element);
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return new Object[0];
    }

}
