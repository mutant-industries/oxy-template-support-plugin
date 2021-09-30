package ool.intellij.plugin.editor.inspection.fix;

import ool.intellij.plugin.file.RelativePathCalculator;
import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.OxyTemplateElementFactory;
import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MissingIncludeDirectiveQuickFix implements LocalQuickFix
{
    private final PsiElement macroCallMissingInclude;

    private final String directiveName;

    private final String includePath;

    public MissingIncludeDirectiveQuickFix(@NotNull PsiElement macroCallMissingInclude,
                                           @NotNull PsiElement macroReference, @NonNls String directiveName)
    {
        this.macroCallMissingInclude = macroCallMissingInclude;
        this.directiveName = directiveName;

        RelativePathCalculator pathCalculator = new RelativePathCalculator(macroCallMissingInclude.getContainingFile()
                .getVirtualFile().getPath(), macroReference.getContainingFile().getVirtualFile().getPath());

        pathCalculator.execute();

        this.includePath = pathCalculator.getResult();
    }

    @NotNull
    @Override
    public String getName()
    {
        return I18nSupport.message("inspection.missing.include.fix", directiveName, includePath);
    }

    @NonNls
    @NotNull
    @Override
    public String getFamilyName()
    {
        return "Oxy template";
    }

    @Override
    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor)
    {
        applyFix(project);
    }

    public void applyFix(Project project)
    {
        final DirectiveStatement includeDirective = OxyTemplateElementFactory.createDirectiveStatement(project,
                directiveName, includePath);

        OxyTemplateHelper.addDirective(includeDirective, macroCallMissingInclude.getContainingFile()
                .getViewProvider().getPsi(OxyTemplate.INSTANCE));
    }

}
