package ool.idea.plugin.editor.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class NotMatchingTagsInspection extends LocalInspectionTool
{
    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Closing macro tag doesn't match opening";
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly)
    {
        final List result = new ArrayList();

        file.acceptChildren(new PsiRecursiveElementVisitor()
        {
            @Override
            public void visitElement(PsiElement element)
            {
                if ((element instanceof MacroTag))
                {
                    MacroTag tag = (MacroTag) element;
                    List<MacroName> list = tag.getMacroNameList();

                    final MacroName closingTagName;

                    if(list.size() <= 1 || (closingTagName = list.get(1)).getNextSibling() == null
                            || closingTagName.getNextSibling().getNode().getElementType() != OxyTemplateTypes.T_XML_CLOSE_TAG_END)
                    {
                        super.visitElement(element);
                        return;
                    }

                    final MacroName openingTagName = list.get(0);

                    if( ! openingTagName.getText().equals(closingTagName.getText()))
                    {
                        result.add(manager.createProblemDescriptor(closingTagName.getPrevSibling().getPrevSibling(),
                                closingTagName.getNextSibling(), NotMatchingTagsInspection.this.getDisplayName(),
                                ProblemHighlightType.GENERIC_ERROR, isOnTheFly, new NotMatchingTagsQuickFix(closingTagName, openingTagName),
                                new NotMatchingTagsQuickFix(openingTagName, closingTagName)

                        ));
                    }
                }

                super.visitElement(element);
            }
        });

        return result.isEmpty() ? super.checkFile(file, manager, isOnTheFly) : ArrayUtil.toObjectArray(result, ProblemDescriptor.class);
    }

}
