package ool.idea.plugin.psi.reference.innerjs;

import com.intellij.lang.javascript.library.JSPredefinedLibraryProvider;
import com.intellij.lang.javascript.psi.resolve.JSElementResolveScopeProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import java.util.Arrays;
import java.util.List;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/30/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsResolveScopeProvider extends JSElementResolveScopeProvider
{
    @NonNls
    private static final List<String> IGNORED_LIBS_LIST = Arrays.asList(
        "DOMCore.js", "DOMXPath.js", "DOMEvents.js", "DOMTraversalAndRange.js",
        "HTML5.js", "DHTML.js", "WebGL.js", "AJAX.js", "lib.d.ts"
    );

    @NotNull
    @Override
    public GlobalSearchScope getElementResolveScope(@NotNull PsiElement element)
    {
        GlobalSearchScope scope = getBaseScope(element);

        for(VirtualFile file : JSPredefinedLibraryProvider.getAllPredefinedLibraryFiles())
        {
            if(IGNORED_LIBS_LIST.contains(file.getName()))
            {
                continue;
            }

            scope = scope.uniteWith(GlobalSearchScope.fileScope(element.getProject(), file));
        }

        return scope;
    }

    @Nullable
    @Override
    public GlobalSearchScope getResolveScope(@NotNull VirtualFile file, Project project)
    {
        return null;    // never called
    }

    @NotNull
    protected GlobalSearchScope getBaseScope(@NotNull PsiElement element)
    {
        // Not working actually - https://devnet.jetbrains.com/thread/460196
        return GlobalSearchScope.getScopeRestrictedByFileTypes(ProjectScope.getProjectScope(element.getProject()),
                OxyTemplateFileType.INSTANCE);
    }

}
