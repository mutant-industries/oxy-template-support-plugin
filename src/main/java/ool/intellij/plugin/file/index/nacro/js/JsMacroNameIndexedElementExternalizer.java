package ool.intellij.plugin.file.index.nacro.js;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * 1/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameIndexedElementExternalizer implements KeyDescriptor<JsMacroNameIndexedElement>
{
    @Override
    public void save(@NotNull DataOutput out, JsMacroNameIndexedElement value) throws IOException
    {
        out.writeInt(value.getOffsetInFile());
        out.writeBoolean(value.isMacro());
    }

    @Override
    public JsMacroNameIndexedElement read(@NotNull DataInput in) throws IOException
    {
        int offsetInFile = in.readInt();
        boolean isMacro = in.readBoolean();

        return new JsMacroNameIndexedElement(isMacro, offsetInFile);
    }

    @Override
    public int getHashCode(JsMacroNameIndexedElement value)
    {
        return value.hashCode();
    }

    @Override
    public boolean isEqual(JsMacroNameIndexedElement val1, JsMacroNameIndexedElement val2)
    {
        return val1.equals(val2);
    }

}
