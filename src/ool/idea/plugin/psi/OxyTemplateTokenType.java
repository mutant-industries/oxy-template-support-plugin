package ool.idea.plugin.psi;

import com.intellij.psi.tree.IElementType;
import ool.idea.plugin.lang.OxyTemplate;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateTokenType extends IElementType
{
    public OxyTemplateTokenType(@NotNull @NonNls String debugName)
    {
        super(debugName, OxyTemplate.INSTANCE);
    }

    @Override
    public String toString()
    {
        return WordUtils.capitalize(super.toString().toLowerCase().replace("t_", "").replaceAll("_", " "));
    }

}
