package ool.idea.plugin.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.util.IncorrectOperationException;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
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

        if(multiResolve.length == 1)
        {
            return multiResolve[0].getElement();
        }

        return null;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
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
                .resolveWithCaching(this, MacroReferenceResolver.INSTANCE, false, false, referencingElement.getContainingFile());
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(startOffset, endOffset);
    }


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

    private static class MacroReferenceResolver implements ResolveCache.PolyVariantContextResolver<MacroReference>
    {
        static final MacroReferenceResolver INSTANCE = new MacroReferenceResolver();

        @NotNull
        @Override
        public ResolveResult[] resolve(@NotNull MacroReference ref, @NotNull PsiFile containingFile, boolean incompleteCode)
        {
            String elemntText = ref.getElement().getText();
            String macroName = elemntText.substring(elemntText.indexOf('\"') + 1, ref.endOffset);

            List<PsiElement> elements = OxyTemplateIndexUtil.getMacroNameReferences(macroName, ref.getElement().getProject());

            ResolveResult[] resolveResults = new ResolveResult[elements.size()];

            for(int i = 0; i < elements.size(); i++)
            {
                resolveResults[i] = new PsiElementResolveResult(elements.get(i));
            }

            return resolveResults;
        }

    }

}
