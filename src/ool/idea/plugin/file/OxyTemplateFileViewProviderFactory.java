package ool.idea.plugin.file;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFileViewProviderFactory implements FileViewProviderFactory
{
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile virtualFile, Language language,
                                                   @NotNull PsiManager psiManager, boolean physical)
    {
        return new OxyTemplateFileViewProvider(psiManager, virtualFile, physical);
    }

}
