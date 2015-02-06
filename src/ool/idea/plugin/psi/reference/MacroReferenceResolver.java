package ool.idea.plugin.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import org.jetbrains.annotations.NotNull;

/**
 * 2/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class MacroReferenceResolver implements ResolveCache.PolyVariantContextResolver<PsiPolyVariantReference>
{
    public static final MacroReferenceResolver INSTANCE = new MacroReferenceResolver();

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull PsiPolyVariantReference ref, @NotNull PsiFile containingFile, boolean incompleteCode)
    {
        String elemntText = ref.getElement().getText();
        String macroName = elemntText.substring(elemntText.indexOf('\"') + 1, ref.getRangeInElement().getEndOffset());

        List<PsiElement> elements = OxyTemplateIndexUtil.getMacroNameReferences(MacroIndex.normalizeMacroName(macroName), ref.getElement().getProject());

        ResolveResult[] resolveResults = new ResolveResult[elements.size()];

        for(int i = 0; i < elements.size(); i++)
        {
            resolveResults[i] = new PsiElementResolveResult(elements.get(i));
        }

        return resolveResults;
    }

}
