package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.visitor.DirectiveStatementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly)
    {
        final List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();

        new DirectiveStatementVisitor()
        {
            @Override
            public void visitDirectiveParamFileReference(@NotNull DirectiveParamFileReference fileReference)
            {
                for (PsiReference reference : fileReference.getReferences())
                {
                    if (reference instanceof FileReference && reference.resolve() == null)
                    {
                        result.add(manager.createProblemDescriptor(reference.getElement(), reference.getRangeInElement(), getDisplayName(),
                                ProblemHighlightType.GENERIC_ERROR, isOnTheFly, ((FileReference) reference).getQuickFixes()));
                    }
                }
            }
        }.visitFile(file);

        return result.isEmpty() ? super.checkFile(file, manager, isOnTheFly) : ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
    }

}
