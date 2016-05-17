package ool.intellij.plugin.editor.usages.search;

import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.RequestResultProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableReferenceSearch extends OxyTemplateReferenceSearch
{
    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<PsiReference> consumer)
    {
        if ( ! (queryParameters.getElementToSearch() instanceof GlobalVariableDefinition))
        {
            return;
        }

        final GlobalVariableDefinition target = (GlobalVariableDefinition) queryParameters.getElementToSearch();
        SearchScope scope = restrictScopeToOxyTemplates(queryParameters.getEffectiveSearchScope());

        queryParameters.getOptimizer().searchWord(target.getName(), scope, UsageSearchContext.IN_CODE, true, target.getExpression(),
                new RequestResultProcessor()
                {
                    @Override
                    public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer)
                    {
                        PsiReference reference;

                        if (element instanceof JSReferenceExpression && (reference = element.getReference()) != null)
                        {
                            PsiElement resolveResult = reference.resolve();

                            if (resolveResult instanceof GlobalVariableDefinition && target.getExpression()
                                    .isEquivalentTo(((GlobalVariableDefinition) resolveResult).getExpression()))
                            {
                                return consumer.process(reference);
                            }

                        }

                        return true;
                    }
                }
        );
    }

}
