package ool.idea.plugin.editor.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ool.idea.plugin.file.RelativePathCalculator;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.OxyTemplateHelper;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MissingIncludeDirectiveQuickFix  implements LocalQuickFix
{
    private final PsiElement macroCallMissingInclude;

    private final String directiveType;

    private final String includePath;

    public MissingIncludeDirectiveQuickFix(@NotNull PsiElement macroCallMissingInclude,
                                           @NotNull PsiElement macroReference, String directiveType)
    {
        this.macroCallMissingInclude = macroCallMissingInclude;
        this.directiveType = directiveType;

        RelativePathCalculator pathCalculator = new RelativePathCalculator(macroCallMissingInclude.getContainingFile()
                .getVirtualFile().getPath(), macroReference.getContainingFile().getVirtualFile().getPath());

        pathCalculator.execute();

        this.includePath = pathCalculator.getResult();
    }

    @NotNull
    @Override
    public String getName()
    {
        return I18nSupport.message("inspection.missing.include.fix", directiveType, includePath);
    }

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
                directiveType, includePath);

        OxyTemplateHelper.addDirective(includeDirective, macroCallMissingInclude.getContainingFile()
                .getViewProvider().getPsi(OxyTemplate.INSTANCE));
    }

}
