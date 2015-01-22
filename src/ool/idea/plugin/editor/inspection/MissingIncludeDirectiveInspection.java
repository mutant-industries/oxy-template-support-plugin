package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.editor.inspection.fix.MissingIncludeDirectiveQuickFix;
import ool.idea.plugin.psi.impl.OxyTemplatePsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MissingIncludeDirectiveInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Missing include directive";
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile file, @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly)
    {
        final List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();

        Iterator<Map.Entry<PsiElement, JSProperty>> iterator = OxyTemplatePsiUtil.getUsedJsMacros(file).entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry<PsiElement, JSProperty> pair = iterator.next();

            PsiElement macroCall = pair.getKey();
            PsiElement reference = pair.getValue();

            if(OxyTemplatePsiUtil.isJsMacroMissingInclude(file, reference))
            {
                result.add(manager.createProblemDescriptor(macroCall, TextRange.create(0, macroCall.getTextLength()),
                        MissingIncludeDirectiveInspection.this.getDisplayName(),
                        ProblemHighlightType.ERROR, isOnTheFly, new MissingIncludeDirectiveQuickFix(macroCall, reference, "include"),
                        new MissingIncludeDirectiveQuickFix(macroCall, reference, "include_once")));
            }
        }

        return result.isEmpty() ? super.checkFile(file, manager, isOnTheFly) : ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
    }

}
