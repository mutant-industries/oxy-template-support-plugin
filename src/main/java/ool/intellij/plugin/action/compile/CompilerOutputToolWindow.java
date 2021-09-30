package ool.intellij.plugin.action.compile;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 9/29/21
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompilerOutputToolWindow implements ToolWindowFactory, DumbAware
{
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow)
    {
        project.getService(CompiledPreviewController.class).initToolWindow(toolWindow);
    }

}
