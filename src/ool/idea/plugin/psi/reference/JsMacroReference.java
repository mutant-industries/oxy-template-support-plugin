package ool.idea.plugin.psi.reference;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.MacroNameIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroReference extends MacroReference<MacroNameIdentifier> implements PsiPolyVariantReference
{
    private final JSElement[] references;

    public JsMacroReference(@NotNull MacroNameIdentifier macroNameIdentifier, @NotNull JSElement[] references)
    {
        super(macroNameIdentifier);
        this.references = references;
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return references.length == 1 ? references[0] : null;
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
        ResolveResult[] resolveResults = new ResolveResult[references.length];

        for(int i = 0; i < references.length; i++)
        {
            resolveResults[i] = new JSResolveResult(references[i]);
        }

        return resolveResults;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
    {
        return referencedIdentifier.setName(newElementName);
    }

}
