package ool.intellij.plugin.editor.inspection.fix;

import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 5/11/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class DeleteElementQuickFix implements LocalQuickFix
{
    @NotNull
    @Override
    public String getName()
    {
        return I18nSupport.message("inspection.redundant.include.fix");
    }

    @NonNls
    @NotNull
    @Override
    public String getFamilyName()
    {
        return "Oxy template";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
    {
        descriptor.getPsiElement().delete();
    }

}
