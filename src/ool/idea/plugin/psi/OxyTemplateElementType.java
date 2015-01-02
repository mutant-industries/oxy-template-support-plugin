package ool.idea.plugin.psi;

import com.intellij.psi.tree.IElementType;
import ool.idea.plugin.OxyTemplate;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateElementType extends IElementType
{
    public OxyTemplateElementType(@NotNull @NonNls String debugName)
    {
        super(debugName, OxyTemplate.INSTANCE);
    }

}
