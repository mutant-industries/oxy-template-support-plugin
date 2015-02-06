package ool.idea.plugin.editor.gotosymbol;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.model.gotosymbol.GoToSymbolProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 1/24/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroNavigationItem extends GoToSymbolProvider.BaseNavigationItem
{
    private final String name;

    private final String fullyQualifiedName;

    private final String namespace;

    public MacroNavigationItem(@NotNull PsiElement psiElement, @NotNull String fullyQualifiedName)
    {
        super(psiElement, fullyQualifiedName.replaceFirst(".+\\.", ""), null);

        this.fullyQualifiedName = fullyQualifiedName;

        name = fullyQualifiedName.replaceFirst(".+\\.", "");
        namespace = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.'));
    }

    public String getFullyQualifiedName()
    {
        return fullyQualifiedName;
    }

    @Override
    public ItemPresentation getPresentation()
    {
        return new MacroDefinitionPresentation(name, namespace, getContainingFile().getName());
    }

}
