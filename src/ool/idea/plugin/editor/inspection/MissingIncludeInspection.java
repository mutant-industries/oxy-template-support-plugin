package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.editor.inspection.fix.MissingIncludeDirectiveQuickFix;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.OxyTemplateHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MissingIncludeInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return I18nSupport.message("inspection.missing.include.display.name");
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile file, @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly)
    {
        final List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();

        for (Map.Entry<PsiElement, JSElement> pair : OxyTemplateHelper.getUsedJsMacros(file).entrySet())
        {
            PsiElement macroCall = pair.getKey();
            PsiElement reference = pair.getValue();

            if (OxyTemplateHelper.isJsMacroMissingInclude(file, reference))
            {
                result.add(manager.createProblemDescriptor(macroCall, TextRange.create(0, macroCall.getTextLength()), getDisplayName(),
                        ProblemHighlightType.ERROR, isOnTheFly, new MissingIncludeDirectiveQuickFix(macroCall, reference, "include_once"),
                        new MissingIncludeDirectiveQuickFix(macroCall, reference, "include")));
            }
        }

        return result.isEmpty() ? super.checkFile(file, manager, isOnTheFly) : ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
    }

}
