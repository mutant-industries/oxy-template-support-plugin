package ool.idea.plugin.file.index.globals;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorIntegerDescriptor;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalsIndex extends FileBasedIndexExtension<String, Integer>
{
    @NonNls
    public static final ID<String, Integer> INDEX_ID = ID.create("oxy.jsGlobals");
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();
    private final EnumeratorIntegerDescriptor dataExternalizer = new EnumeratorIntegerDescriptor();

    @NotNull
    @Override
    public ID<String, Integer> getName()
    {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, Integer, FileContent> getIndexer()
    {
        return new JsGlobalsDataIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor()
    {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Integer> getValueExternalizer()
    {
        return dataExternalizer;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter()
    {
        return new DefaultFileTypeSpecificInputFilter(StdFileTypes.JAVA);
    }

    @Override
    public boolean dependsOnFileContent()
    {
        return true;
    }

    @Override
    public int getVersion()
    {
        return 0;
    }

}
