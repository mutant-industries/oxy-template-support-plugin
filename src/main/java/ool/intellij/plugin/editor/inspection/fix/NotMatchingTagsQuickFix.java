package ool.intellij.plugin.editor.inspection.fix;

import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.MacroName;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class NotMatchingTagsQuickFix implements IntentionAction, LocalQuickFix
{
    private final MacroName toBeFixed;
    private final MacroName replacement;

    public NotMatchingTagsQuickFix(@NotNull MacroName toBeFixed, @NotNull MacroName replacement)
    {
        this.toBeFixed = toBeFixed;
        this.replacement = replacement;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
    {
        applyFix(project);
    }

    public void applyFix(@NotNull Project project)
    {
        CachingReference.getManipulator(toBeFixed)
                .handleContentChange(toBeFixed, TextRange.create(0, toBeFixed.getTextLength()), replacement.getText());
    }

    @NotNull
    @Override
    public String getName()
    {
        String tagType = replacement.getStartOffsetInParent() < toBeFixed.getStartOffsetInParent() ? I18nSupport.message("tag.closing") : I18nSupport.message("tag.opening");

        return I18nSupport.message("annotator.not.matching.tags.reference.fix", tagType, replacement.getText());
    }

    @NotNull
    @Override
    public String getText()
    {
        return getName();
    }

    @NonNls
    @NotNull
    @Override
    public String getFamilyName()
    {
        return "Oxy template";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file)
    {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException
    {
        applyFix(project);
    }

    @Override
    public boolean startInWriteAction()
    {
        return true;
    }

}
