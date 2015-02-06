package ool.idea.plugin.editor.gotosymbol;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroGotoSymbolContributor implements GotoClassContributor
{
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems)
    {
        Set<String> names = new HashSet<String>();

        for (Map.Entry<String, PsiElement> entry : OxyTemplateIndexUtil.getMacros(project).entries())
        {
            names.add(entry.getKey());
        }

        return names.toArray(new String[names.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems)
    {
        List<PsiElement> items = OxyTemplateIndexUtil.getMacroNameReferences(name, project);

        NavigationItem[] navigationItems = new NavigationItem[items.size()];

        for(int i = 0; i < items.size(); i++)
        {
            navigationItems[i] = new MacroNavigationItem(items.get(i), name);
        }

        return navigationItems;
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem item)
    {
        if(item instanceof MacroNavigationItem)
        {
            return ((MacroNavigationItem) item).getFullyQualifiedName();
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator()
    {
        return ".";
    }

}
