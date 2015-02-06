package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.OxyTemplateElementVisitor;
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
                for (PsiReference reference : fileReference.getReferences())
                {
                    if (reference instanceof FileReference && reference.resolve() == null)
                    {
                        holder.registerProblem(reference.getElement(), getDisplayName(),
                                ProblemHighlightType.GENERIC_ERROR, ((FileReference) reference).getQuickFixes());
                    }
                }
            }
        };
    }

}
