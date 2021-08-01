package ool.intellij.plugin.action.compile;

import ool.intellij.plugin.file.OxyTemplateFile;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewAction extends DumbAwareAction
{
    @Override
    public void update(@NotNull AnActionEvent e)
    {
        PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(psiFile instanceof OxyTemplateFile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        PsiFile originalFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
        Project project = LangDataKeys.PROJECT.getData(e.getDataContext());

        if (project == null || ! (originalFile instanceof OxyTemplateFile))
        {
            return;
        }

        project.getComponent(CompiledPreviewController.class).showCompiledCode(originalFile);
    }

}
