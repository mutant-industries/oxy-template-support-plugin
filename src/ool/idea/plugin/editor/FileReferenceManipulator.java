package ool.idea.plugin.editor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.DirectiveParamFileReference;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class FileReferenceManipulator extends AbstractElementManipulator<DirectiveParamFileReference>
{
    @Override
    public DirectiveParamFileReference handleContentChange(DirectiveParamFileReference element, TextRange range, String newContent)
            throws IncorrectOperationException
    {
        PsiElement child = element.getFirstChild();

        if ((child instanceof LeafElement))
        {
            ((LeafElement) child).replaceWithText(newContent);
        }

        return element;
    }

}
