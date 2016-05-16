package ool.intellij.plugin.editor.inspection;

import java.util.Arrays;

import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.DirectiveParamFileReference;
import ool.intellij.plugin.psi.OxyTemplateElementVisitor;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnresolvedIncludeInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return I18nSupport.message("inspection.unresolved.include.display.name");
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly)
    {
        return new OxyTemplateElementVisitor()
        {
            @Override
            public void visitDirectiveParamFileReference(@NotNull DirectiveParamFileReference fileReference)
            {
                Arrays.stream(fileReference.getReferences())
                        .filter(reference -> reference instanceof FileReference && reference.resolve() == null)
                        .forEach(reference -> holder.registerProblem(reference.getElement(), getDisplayName(),
                            ProblemHighlightType.GENERIC_ERROR, ((FileReference) reference).getQuickFixes()));
            }
        };
    }

}
