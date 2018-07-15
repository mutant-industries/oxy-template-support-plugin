package ool.intellij.plugin.editor.usages.search;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * Usages of some getters are not handled by methodReferenceSearch, but by normal reference search instead.
 *
 * 5/17/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaSpecialGetterReferenceSearch extends OxyTemplateReferenceSearch
{
    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor)
    {
        if (searchParameters.getElementToSearch() instanceof PsiMethod)
        {
            JavaGetterReferenceSearch.doSearch((PsiMethod) searchParameters.getElementToSearch(), searchParameters.getOptimizer(),
                    searchParameters.getEffectiveSearchScope());
        }
    }
}
