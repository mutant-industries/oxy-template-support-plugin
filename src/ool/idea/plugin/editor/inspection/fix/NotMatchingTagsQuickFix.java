package ool.idea.plugin.editor.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import ool.idea.plugin.psi.MacroName;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class NotMatchingTagsQuickFix implements LocalQuickFix
{
    private final MacroName toBeFixed;
    private final MacroName replacement;

    public NotMatchingTagsQuickFix(@NotNull MacroName toBeFixed, @NotNull MacroName replacement)
    {
        this.toBeFixed = toBeFixed;
        this.replacement = replacement;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "change " + (replacement.getStartOffsetInParent() < toBeFixed.getStartOffsetInParent()
            ? "closing" : "opening") + " tag name to " + replacement.getText();
    }

    @NotNull
    @Override
    public String getFamilyName()
    {
        return "Oxy template";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
    {
        CachingReference.getManipulator(toBeFixed)
                .handleContentChange(toBeFixed, TextRange.create(0, toBeFixed.getTextLength()), replacement.getText());
    }

}
