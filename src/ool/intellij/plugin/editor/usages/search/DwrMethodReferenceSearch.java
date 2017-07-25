package ool.intellij.plugin.editor.usages.search;

import ool.intellij.plugin.psi.reference.js.DwrReferenceResolver;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.RequestResultProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * Searches usages of dwr methods (@RemoteMethod) in javascript
 *
 * 7/25/17
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DwrMethodReferenceSearch extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters>
{
    public DwrMethodReferenceSearch()
    {
        super(true);
    }

    @Override
    public void processQuery(@NotNull MethodReferencesSearch.SearchParameters queryParameters,
                             @NotNull Processor<PsiReference> consumer)
    {
        PsiMethod method = queryParameters.getMethod();

        if ( ! method.getModifierList().hasModifierProperty(PsiModifier.PUBLIC) || ! DwrReferenceResolver.isDwrMethod(method))
        {
            return;
        }

        String query = method.getName();
        SearchScope scope = queryParameters.getEffectiveSearchScope();

        if (scope instanceof GlobalSearchScope)
        {
            // intersect(union(original scope, js file type scope), not(java file type scope))
            GlobalSearchScope jsFilesScope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(method.getProject()),
                    JavaScriptFileType.INSTANCE);

            scope = jsFilesScope.uniteWith((GlobalSearchScope) scope).intersectWith(GlobalSearchScope.notScope(GlobalSearchScope
                    .getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(method.getProject()), JavaFileType.INSTANCE)));
        }

        queryParameters.getOptimizer().searchWord(query, scope, UsageSearchContext.IN_CODE, true, method,
                new RequestResultProcessor()
                {
                    @Override
                    public boolean processTextOccurrence(@NotNull PsiElement element, int offsetInElement, @NotNull Processor<PsiReference> consumer)
                    {
                        PsiElement reference;

                        return ! ((element instanceof JSReferenceExpression && (reference = ((JSReferenceExpression) element).resolve()) != null)
                                && reference.isEquivalentTo(method)) || consumer.process((JSReferenceExpression) element);

                    }
                }
        );
    }

}
