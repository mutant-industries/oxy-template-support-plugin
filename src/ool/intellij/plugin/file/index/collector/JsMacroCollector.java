package ool.intellij.plugin.file.index.collector;

import ool.intellij.plugin.file.index.nacro.js.JsMacroNameIndexedElement;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroCollector extends MacroCollector<JSElement, JsMacroNameIndexedElement>
{
    public JsMacroCollector(@NotNull Project project)
    {
        super(project);
    }

    @Override
    public boolean process(VirtualFile file, JsMacroNameIndexedElement macroNameElement)
    {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

        if (psiFile != null)
        {
            PsiElement psiElement = psiFile.getViewProvider().findElementAt(macroNameElement.getOffsetInFile() - 1);

            if (psiElement != null && psiElement.getParent() instanceof JSElement)
            {
                result.add((JSElement) psiElement.getParent());
            }
        }

        return true;
    }

}
