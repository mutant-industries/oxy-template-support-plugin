package ool.intellij.plugin.editor.inspection;

import java.util.Map;

import ool.intellij.plugin.action.IncludeOptimizer;
import ool.intellij.plugin.editor.inspection.fix.DeleteElementQuickFix;
import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.DirectiveParamFileReference;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.OxyTemplateElementVisitor;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.web.template.impl.chunk.directive.IncludeOnceDirective;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
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

                if ( ! IncludeOnceDirective.NAME.equals(directiveStatement.getName()) ||
                        IncludeOptimizer.ignore(directiveStatement))
                {
                    return;
                }

                PsiReference[] references = fileReference.getReferences();
                PsiFile referencedFile = null;

                for (PsiReference reference : references)
                {
                    if (reference instanceof FileReference && reference.resolve() instanceof PsiFile)
                    {
                        referencedFile = (PsiFile) reference.resolve();

                        break;
                    }
                }

                if (referencedFile == null)
                {
                    return;
                }

                if ( ! directiveStatement.getContainingFile().getVirtualFile().getPath()
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

                holder.registerProblem(directiveStatement, getDisplayName(), ProblemHighlightType.LIKE_UNUSED_SYMBOL, new DeleteElementQuickFix());
            }
        };
    }

}
