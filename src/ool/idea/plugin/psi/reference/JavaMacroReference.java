package ool.idea.plugin.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.MacroName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroReference implements PsiReference
{
    // TODO debug.generatedCode
    private final String prefix = "oxy.";

    private final PsiClass psiClass;

    private final MacroName macroName;

    public JavaMacroReference(@NotNull MacroName macroName, @NotNull PsiClass psiClass)
    {
        this.psiClass = psiClass;
        this.macroName = macroName;
    }

    @Override
    public PsiElement getElement()
    {
        return macroName;
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(prefix.length(), macroName.getText().length());
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return psiClass.getNameIdentifier();
    }

    @NotNull
    @Override
    public String getCanonicalText()
    {
        return macroName.getText();
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
        return element instanceof PsiClass && psiClass.isEquivalentTo(((PsiClass)element).getNameIdentifier());
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return new Object[0];
    }

    @Override
    public boolean isSoft()
    {
        return true;
    }

}
