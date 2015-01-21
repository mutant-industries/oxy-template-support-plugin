package ool.idea.plugin.file.index.collector;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroCollector extends MacroCollector<PsiClass, Void> implements Processor<VirtualFile>
{
    public JavaMacroCollector(@NotNull Project project)
    {
        super(project);
    }

    @Override
    public boolean process(VirtualFile file)
    {
        return process(file, null);
    }

    @Override
    public boolean process(VirtualFile file, Void value)
    {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

        if(psiFile != null)
        {
            PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);

            if(psiClass != null)
            {
                result.add(psiClass);
            }
        }

        return true;
    }

}
