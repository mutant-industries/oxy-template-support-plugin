package ool.intellij.plugin.file.index.nacro.js;

import ool.intellij.plugin.file.type.OxyTemplateFileType;

import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameIndex extends FileBasedIndexExtension<String, JsMacroNameIndexedElement>
{
    @NonNls
    public static final ID<String, JsMacroNameIndexedElement> INDEX_ID = ID.create("oxy.jsMacroName");
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();
    private final JsMacroNameIndexedElementExternalizer dataExternalizer = new JsMacroNameIndexedElementExternalizer();

    @NotNull
    @Override
    public ID<String, JsMacroNameIndexedElement> getName()
    {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, JsMacroNameIndexedElement, FileContent> getIndexer()
    {
        return new JsMacroNameDataIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor()
    {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<JsMacroNameIndexedElement> getValueExternalizer()
    {
        return dataExternalizer;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter()
    {
        return new DefaultFileTypeSpecificInputFilter(OxyTemplateFileType.INSTANCE);
    }

    @Override
    public boolean dependsOnFileContent()
    {
        return true;
    }

    @Override
    public int getVersion()
    {
        return 1;
    }

}
