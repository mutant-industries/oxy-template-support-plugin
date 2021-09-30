package ool.intellij.plugin.editor.usages.search;

import ool.intellij.plugin.file.type.OxyTemplateFileType;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;

/**
 * 5/17/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class OxyTemplateReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>
{
    public OxyTemplateReferenceSearch()
    {
        super(true);
    }

    static SearchScope restrictScopeToOxyTemplates(SearchScope originalScope)
    {
        return originalScope instanceof GlobalSearchScope ? GlobalSearchScope
                .getScopeRestrictedByFileTypes((GlobalSearchScope) originalScope, OxyTemplateFileType.INSTANCE) : originalScope;
    }

}
