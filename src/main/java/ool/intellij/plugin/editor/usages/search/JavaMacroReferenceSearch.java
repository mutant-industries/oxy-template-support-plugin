package ool.intellij.plugin.editor.usages.search;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.file.index.nacro.MacroIndex;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
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
public class JavaMacroReferenceSearch extends OxyTemplateReferenceSearch
{
    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor)
    {
        final PsiElement target = searchParameters.getElementToSearch();
        PsiClass psiClass;

        if ( ! (target instanceof PsiClass) || ! OxyTemplateIndexUtil.isMacro(psiClass = (PsiClass) target))
        {
            return;
        }

        SearchScope scope = restrictScopeToOxyTemplates(searchParameters.getEffectiveSearchScope());

        assert psiClass.getName() != null;

        final String query = StringUtil.decapitalize(psiClass.getName().replaceFirst(MacroIndex.JAVA_MACRO_SUFFIX + "$", ""));

        searchParameters.getOptimizer().searchWord(query, scope, UsageSearchContext.IN_CODE, true, target);
    }
}
