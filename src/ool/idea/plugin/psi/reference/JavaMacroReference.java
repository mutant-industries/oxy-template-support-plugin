package ool.idea.plugin.psi.reference;

import com.intellij.psi.PsiClass;
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
public class JavaMacroReference extends MacroReference<MacroNameIdentifier>
{
    protected final PsiClass reference;

    public JavaMacroReference(@NotNull MacroNameIdentifier macroNameIdentifier, @NotNull PsiClass reference)
    {
        super(macroNameIdentifier);
        this.reference = reference;
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return reference;
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return reference.isEquivalentTo(element);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
    {
        throw new IncorrectOperationException("handleElementRename not implemented yet");
    }

}
