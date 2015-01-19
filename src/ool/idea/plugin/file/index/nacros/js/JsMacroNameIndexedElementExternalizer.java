package ool.idea.plugin.file.index.nacros.js;

import com.google.gson.Gson;
import com.intellij.util.io.IOUtil;
import com.intellij.util.io.KeyDescriptor;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 1/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameIndexedElementExternalizer implements KeyDescriptor<JsMacroNameIndexedElement>
{
    @Override
    public void save(DataOutput out, JsMacroNameIndexedElement value) throws IOException
    {
        IOUtil.writeUTF(out, new Gson().toJson(value));
    }

    @Override
    public JsMacroNameIndexedElement read(DataInput in) throws IOException
    {
        return new Gson().fromJson(IOUtil.readUTF(in), JsMacroNameIndexedElement.class);
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
