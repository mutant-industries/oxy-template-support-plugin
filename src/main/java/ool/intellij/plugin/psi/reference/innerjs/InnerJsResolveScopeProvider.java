package ool.intellij.plugin.psi.reference.innerjs;

import ool.intellij.plugin.file.type.OxyTemplateFileType;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;

import com.intellij.lang.javascript.library.JSPredefinedLibraryProvider;
import com.intellij.lang.javascript.psi.resolve.JSElementResolveScopeProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/30/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsResolveScopeProvider implements JSElementResolveScopeProvider
{
    @Nullable
    @Override
    public GlobalSearchScope getElementResolveScope(@NotNull PsiElement element)
    {
        PsiFile psiFile = element.getContainingFile();

        if (psiFile == null || ! isApplicable(psiFile))
        {
            return null;
        }

        GlobalSearchScope scope = getBaseScope(element);

        for (ScriptingLibraryModel model : JSPredefinedLibraryProvider.getAllPredefinedLibraries(element.getProject()))
        {
            if ( ! model.getName().equals("oxy-predefined") && ! model.getName().equals("Nashorn"))
            {
                continue;
            }

            for (VirtualFile file : model.getAllFiles())
            {
                scope = scope.uniteWith(GlobalSearchScope.fileScope(element.getProject(), file));
            }
        }

        return scope;
    }

    @NotNull
    protected GlobalSearchScope getBaseScope(@NotNull PsiElement element)
    {
        // Not working actually - https://devnet.jetbrains.com/thread/460196
        return GlobalSearchScope.getScopeRestrictedByFileTypes(ProjectScope.getProjectScope(element.getProject()),
                OxyTemplateFileType.INSTANCE);
    }

    protected boolean isApplicable(@NotNull PsiFile psiFile)
    {
        return psiFile.getLanguage() == OxyTemplateInnerJs.INSTANCE;
    }

}
