package ool.intellij.plugin.editor.usages;

import ool.intellij.plugin.file.type.OxyTemplateFileType;
import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
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
public class OxyTemplateReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>
{
    public OxyTemplateReferenceSearch()
    {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<PsiReference> consumer)
    {
        final PsiElement target = queryParameters.getElementToSearch();
        PsiClass psiClass;

        if (target instanceof PsiClass && (psiClass = (PsiClass) target).getName().endsWith("Macro"))
        {
            SearchScope scope = restrictScopeToOxyTemplates(queryParameters.getEffectiveSearchScope());

            final String query = StringUtil.decapitalize(psiClass.getName().replaceFirst("Macro$", ""));

            queryParameters.getOptimizer().searchWord(query, scope, UsageSearchContext.IN_CODE, true, target,
                new RequestResultProcessor()
                {
                    @Override
                    public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer)
                    {
                        PsiReference reference;
                        if ((element instanceof JSReferenceExpression || element instanceof MacroName)
                                && (reference = element.getReference()) != null)
                        {
                            PsiElement resolveResult;

                            if ((resolveResult = reference.resolve()) != null && resolveResult.isEquivalentTo(target)
                                    && consumer.process(reference)) ;
                        }

                        return true;
                    }
                }
            );
        }
        else if (target instanceof GlobalVariableDefinition)
        {
            final GlobalVariableDefinition fakeTarget = (GlobalVariableDefinition) target;

            SearchScope scope = restrictScopeToOxyTemplates(queryParameters.getEffectiveSearchScope());

            final String query = fakeTarget.getName();

            queryParameters.getOptimizer().searchWord(query, scope, UsageSearchContext.IN_CODE, true, fakeTarget.getExpression(),
                new RequestResultProcessor()
                {
                    @Override
                    public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer)
                    {
                        PsiReference reference;

                        if (element instanceof JSReferenceExpression && (reference = element.getReference()) != null)
                        {
                            PsiElement resolveResult = reference.resolve();

                            if (resolveResult instanceof GlobalVariableDefinition && fakeTarget.getExpression()
                                    .isEquivalentTo(((GlobalVariableDefinition) resolveResult).getExpression()) && consumer.process(reference)) ;
                        }

                        return true;
                    }
                }
            );
        }
    }

    private static SearchScope restrictScopeToOxyTemplates(SearchScope originalScope)
    {
        return originalScope instanceof GlobalSearchScope ? GlobalSearchScope
                .getScopeRestrictedByFileTypes((GlobalSearchScope) originalScope, OxyTemplateFileType.INSTANCE) : originalScope;
    }

}
