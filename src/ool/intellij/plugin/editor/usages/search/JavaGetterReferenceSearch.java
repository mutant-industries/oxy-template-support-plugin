package ool.intellij.plugin.editor.usages.search;

import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.RequestResultProcessor;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import com.sun.xml.internal.ws.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Searches usages of getters on entities / extenders etc. in templates.
 *
 * 5/17/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaGetterReferenceSearch extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters>
{
    public JavaGetterReferenceSearch()
    {
        super(true);
    }

    @Override
    public void processQuery(@NotNull MethodReferencesSearch.SearchParameters queryParameters,
                             @NotNull Processor<PsiReference> consumer)
    {
        doSearch(queryParameters.getMethod(), queryParameters.getOptimizer(), queryParameters.getEffectiveSearchScope());
    }

    static void doSearch(@NotNull final PsiMethod method, @NotNull final SearchRequestCollector optimizer, SearchScope effectiveSearchScope)
    {
        if ( ! method.getModifierList().hasModifierProperty(PsiModifier.PUBLIC))
        {
            return;
        }

        String methodName = method.getName();

        if ( ! methodName.matches("((^is)|(^get))[A-Z].*"))
        {
            return;
        }

        String query = StringUtils.decapitalize(methodName.replaceFirst("(^is)|(^get)", ""));
        SearchScope scope = OxyTemplateReferenceSearch.restrictScopeToOxyTemplates(effectiveSearchScope);

        optimizer.searchWord(query, scope, UsageSearchContext.IN_CODE, true, method,
                new RequestResultProcessor()
                {
                    @Override
                    public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer)
                    {
                        PsiElement reference;

                        return ! ((element instanceof JSReferenceExpression && (reference = element.getReference().resolve()) != null)
                                && reference.isEquivalentTo(method)) || consumer.process((JSReferenceExpression) element);

                    }
                }
        );
    }

}
