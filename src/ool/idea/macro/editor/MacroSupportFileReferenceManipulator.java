package ool.idea.macro.editor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.util.IncorrectOperationException;
import ool.idea.macro.psi.MacroSupportDirectiveParamFileReference;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportFileReferenceManipulator extends AbstractElementManipulator<MacroSupportDirectiveParamFileReference>
{
    @Override
    public MacroSupportDirectiveParamFileReference handleContentChange(MacroSupportDirectiveParamFileReference element, TextRange range, String newContent)
            throws IncorrectOperationException
    {
        PsiElement child = element.getFirstChild();

        if ((child instanceof LeafElement))
        {
            ((LeafElement) child).replaceWithText(newContent);
        }

        return element;
    }

    @Override
    public TextRange getRangeInElement(MacroSupportDirectiveParamFileReference element)
    {
        return TextRange.from(0, element.getTextLength());
    }

}
