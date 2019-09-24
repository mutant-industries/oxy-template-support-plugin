package ool.intellij.plugin.psi.reference.innerjs;

import ool.intellij.plugin.lang.CompiledPreview;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewResolveScopeProvider extends InnerJsResolveScopeProvider
{
    @NotNull
    @Override
    protected GlobalSearchScope getBaseScope(@NotNull PsiElement element)
    {
        return GlobalSearchScope.fileScope(element.getProject(), element.getContainingFile().getVirtualFile());
    }

    @Override
    protected boolean isApplicable(@NotNull PsiFile psiFile)
    {
        return psiFile.getLanguage() == CompiledPreview.INSTANCE;
    }

}
