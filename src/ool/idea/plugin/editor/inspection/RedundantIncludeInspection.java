package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Map;
import ool.idea.plugin.action.IncludeOptimizer;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.OxyTemplateElementVisitor;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.web.template.impl.chunk.directive.IncludeOnceDirective;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 3/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class RedundantIncludeInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return I18nSupport.message("inspection.redundant.include.display.name");
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
                final DirectiveStatement directiveStatement = PsiTreeUtil.getParentOfType(fileReference, DirectiveStatement.class);

                assert directiveStatement != null;

                if( ! IncludeOnceDirective.NAME.equals(directiveStatement.getName()) ||
                        IncludeOptimizer.ignore(directiveStatement))
                {
                    return;
                }

                PsiReference[] references = fileReference.getReferences();
                PsiFile referencedFile = null;

                for (PsiReference reference : references)
                {
                    if(reference instanceof FileReference && reference.resolve() instanceof PsiFile)
                    {
                        referencedFile = (PsiFile) reference.resolve();

                        break;
                    }
                }

                if (referencedFile == null)
                {
                    return;
                }

                if( ! directiveStatement.getContainingFile().getVirtualFile().getPath()
                        .equals(referencedFile.getVirtualFile().getPath()))
                {
                    for (Map.Entry<PsiElement, JSElement> entry : OxyTemplateHelper.getUsedJsMacros(fileReference.getContainingFile()).entrySet())
                    {
                        if (entry.getValue().getContainingFile().getVirtualFile().getPath().equals(referencedFile.getVirtualFile().getPath()))
                        {
                            return;
                        }
                    }
                }

                holder.registerProblem(directiveStatement, getDisplayName(), ProblemHighlightType.LIKE_UNUSED_SYMBOL, new LocalQuickFix()
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
                });
            }
        };
    }

}
