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

                if( ! IncludeOnceDirective.NAME.equals(directiveStatement.getName()))
                {
                    return;
                }

                PsiReference[] references = fileReference.getReferences();
                PsiFile referencingFile = null;

                for (PsiReference reference : references)
                {
                    if(reference instanceof FileReference && reference.resolve() instanceof PsiFile)
                    {
                        referencingFile = (PsiFile) reference.resolve();

                        break;
                    }
                }

                if (referencingFile == null)
                {
                    return;
                }

                for (Map.Entry<PsiElement, JSElement> entry : OxyTemplateHelper.getUsedJsMacros(fileReference.getContainingFile()).entrySet())
                {
                    if (entry.getValue().getContainingFile().getVirtualFile().getPath().equals(referencingFile.getVirtualFile().getPath()))
                    {
                        return;
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
