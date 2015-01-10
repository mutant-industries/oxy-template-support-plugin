package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnresolvedDirectiveIncludeInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Unresolved include";
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly)
    {
        final List result = new ArrayList();

        file.acceptChildren(new PsiRecursiveElementVisitor()
        {
            @Override
            public void visitElement(PsiElement element)
            {
                if ((element instanceof DirectiveParamFileReference))
                {
                    for (PsiReference reference : element.getReferences())
                    {
                        if (reference instanceof FileReference && reference.resolve() == null)
                        {
                            result.add(manager.createProblemDescriptor(reference.getElement(), reference.getRangeInElement(),
                                    UnresolvedDirectiveIncludeInspection.this.getDisplayName(),
                                    ProblemHighlightType.GENERIC_ERROR, isOnTheFly, ((FileReference) reference).getQuickFixes()));
                        }
                    }
                }

                super.visitElement(element);
            }
        });

        return result.isEmpty() ? super.checkFile(file, manager, isOnTheFly) : ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
    }

}
