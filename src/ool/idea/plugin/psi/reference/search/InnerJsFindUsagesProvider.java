package ool.idea.plugin.psi.reference.search;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.findUsages.JavaScriptFindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsFindUsagesProvider extends JavaScriptFindUsagesProvider
{
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
    {
        return psiElement.getNode().getElementType() == JSTokenTypes.IDENTIFIER;
    }

}
