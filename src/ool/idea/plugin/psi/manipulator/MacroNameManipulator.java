package ool.idea.plugin.psi.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 1/29/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroNameManipulator extends AbstractElementManipulator<MacroName>
{
    @Override
    public MacroName handleContentChange(@NotNull MacroName element, @NotNull TextRange range, String newContent) throws IncorrectOperationException
    {
        String elementText = element.getText();

        return (MacroName) element.replace(OxyTemplateElementFactory.createMacroName(element.getProject(),
                elementText.substring(0, range.getStartOffset()) + newContent + elementText.substring(range.getEndOffset(), elementText.length())));
    }

}
