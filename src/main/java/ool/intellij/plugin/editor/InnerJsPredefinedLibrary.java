package ool.intellij.plugin.editor;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.intellij.lang.javascript.library.JSCorePredefinedLibrariesProvider;
import com.intellij.lang.javascript.library.JSPredefinedLibraryProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsPredefinedLibrary extends JSPredefinedLibraryProvider
{
    @NonNls
    private static final String LIBRARY_NAME = "oxy-predefined";

    @NonNls
    private String[] jsFiles = {"/js/inner-js-predefined.js"};

    @NotNull
    @Override
    public ScriptingLibraryModel[] getPredefinedLibraries(@NotNull Project project)
    {
        Set<VirtualFile> virtualFiles = getVirtualFiles();

        ScriptingLibraryModel scriptingLibraryModel = ScriptingLibraryModel.createPredefinedLibrary(LIBRARY_NAME,
                virtualFiles.toArray(new VirtualFile[0]), false);

        return new ScriptingLibraryModel[]{scriptingLibraryModel};
    }

    @Override
    public Collection<VirtualFile> getFilesForGlobalsProcessing()
    {
        return getVirtualFiles();
    }

    private Set<VirtualFile> getVirtualFiles()
    {
        Set<VirtualFile> virtualFiles = new HashSet<>();

        for (String libFileName : this.jsFiles)
        {
            URL fileUrl = InnerJsPredefinedLibrary.class.getResource(libFileName);
            virtualFiles.add(VfsUtil.findFileByURL(fileUrl));
        }

        virtualFiles.addAll(JSCorePredefinedLibrariesProvider.getJavaScriptCorePredefinedLibraryFiles());

        return virtualFiles;
    }

}
