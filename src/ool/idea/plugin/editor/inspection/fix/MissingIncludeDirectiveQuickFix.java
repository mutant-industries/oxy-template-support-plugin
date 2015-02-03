package ool.idea.plugin.editor.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;
import ool.idea.plugin.file.RelativePathCalculator;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
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
        return "Add " + directiveType + " \"" + includePath + "\"";
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
        final PsiFile file = macroCallMissingInclude.getContainingFile()
                .getViewProvider().getPsi(OxyTemplate.INSTANCE);
        final DirectiveStatement includeDirective = OxyTemplateElementFactory.createDirectiveStatement(project,
                directiveType, includePath);

        List<DirectiveStatement> statements = PsiTreeUtil.getChildrenOfTypeAsList(file, DirectiveStatement.class);

        if (statements.size() > 0)
        {
            file.addAfter(includeDirective, statements.get(statements.size() - 1));
        }
        else
        {
            file.addBefore(includeDirective, file.getFirstChild());
        }
    }

}
