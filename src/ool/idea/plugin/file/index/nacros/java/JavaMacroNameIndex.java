package ool.idea.plugin.file.index.nacros.java;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroNameIndex extends ScalarIndexExtension<String>
{
    @NonNls
    public static final ID<String, Void> INDEX_ID = ID.create("oxy.javaMacroName");
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Void> getName()
    {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer()
    {
        return new JavaMacroNameDataIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor()
    {
        return keyDescriptor;
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
