package ool.idea.plugin.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ool.idea.plugin.psi.MacroNameIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroReference<T extends PsiElement> implements PsiReference
{
    protected final T referencedElement;

    protected final MacroNameIdentifier macroNameIdentifier;

    public MacroReference(@NotNull MacroNameIdentifier macroNameIdentifier, @NotNull T referencedElement)
    {
        this.referencedElement = referencedElement;
        this.macroNameIdentifier = macroNameIdentifier;
    }

    @Override
    public PsiElement getElement()
    {
        return macroNameIdentifier;
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(0, macroNameIdentifier.getText().length());
    }

    @NotNull
    @Override
    public String getCanonicalText()
    {
        return macroNameIdentifier.getText();
    }

    @Override
    public boolean isSoft()
    {
        return true;
    }

}
