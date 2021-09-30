package ool.intellij.plugin.psi;

import ool.intellij.plugin.lang.OxyTemplate;

import com.intellij.psi.tree.IElementType;
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
        return WordUtils.capitalize(super.toString().toLowerCase().replaceFirst("t_", "").replaceAll("_", " "));
    }

}
