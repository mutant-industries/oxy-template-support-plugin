package ool.intellij.plugin.editor.usages;

import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.OxyTemplatePsiElement;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFindUsagesProvider implements FindUsagesProvider
{
    @Override
    public WordsScanner getWordsScanner()
    {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
    {
        return psiElement instanceof MacroName;
    }

    @Override
    public String getHelpId(@NotNull PsiElement psiElement)
    {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element)
    {
        if (element instanceof OxyTemplatePsiElement)
        {
            return element.getNode().getElementType().toString();
        }

        return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
    }

    @NonNls
    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element)
    {
        String name;

        if (element instanceof PsiNamedElement && (name = ((PsiNamedElement) element).getName()) != null)
        {
            return name;
        }

        return "anonymous";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName)
    {
        return getDescriptiveName(element);
    }

}
