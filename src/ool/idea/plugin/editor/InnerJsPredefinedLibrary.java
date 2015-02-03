package ool.idea.plugin.editor;

import com.intellij.lang.javascript.library.JSPredefinedLibraryProvider;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsPredefinedLibrary extends JSPredefinedLibraryProvider
{
    private static final String LIBRARY_NAME = "oxy-predefined";

    private String[] jsFiles = {
            "/inner-js-predefined.js"
    };

    @Override
    public ScriptingLibraryModel[] getPredefinedLibraries()
    {
        Set virtualFiles = getVirtualFiles();

        ScriptingLibraryModel scriptingLibraryModel = ScriptingLibraryModel.createPredefinedLibrary(LIBRARY_NAME,
                (VirtualFile[]) virtualFiles.toArray(new VirtualFile[virtualFiles.size()]), true);

        return new ScriptingLibraryModel[]{scriptingLibraryModel};
    }

    @Override
    public Set<VirtualFile> getPredefinedLibraryFiles()
    {
        return getVirtualFiles();
    }

    private Set<VirtualFile> getVirtualFiles()
    {
        Set<VirtualFile> virtualFiles = new HashSet<VirtualFile>();

        for (String libFileName : this.jsFiles)
        {
            URL fileUrl = InnerJsPredefinedLibrary.class.getResource(libFileName);
            virtualFiles.add(VfsUtil.findFileByURL(fileUrl));
        }

        return virtualFiles;
    }

}
