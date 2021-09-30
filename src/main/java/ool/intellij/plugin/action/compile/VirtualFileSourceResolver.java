package ool.intellij.plugin.action.compile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import ool.web.template.exception.CouldNotResolveSourceException;
import ool.web.template.source.Source;
import ool.web.template.source.SourceResolver;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 2/27/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class VirtualFileSourceResolver implements SourceResolver
{
    private final String path;

    public VirtualFileSourceResolver(@NotNull String path)
    {
        this.path = path;
    }

    @Override
    public Source resolveSource(@NotNull String templateName) throws CouldNotResolveSourceException
    {
        VirtualFile virtualFile;
        String fileName = getFileName(templateName);

        if ((virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(fileName))) == null)
        {
            throw new CouldNotResolveSourceException(fileName);
        }

        FileContentReader readAction = new FileContentReader(virtualFile);
        String pathChange = FilenameUtils.getPath(templateName);

        ApplicationManager.getApplication().runReadAction(readAction);

        VirtualFileSourceResolver resolver = pathChange.isEmpty() ? this :
                new VirtualFileSourceResolver(FilenameUtils.concat(this.path, pathChange));

        return new Source(readAction.getResult().toString(), resolver);
    }

    @Override
    public URL resolvePathToTemplate(@NotNull String templateName) throws CouldNotResolveSourceException
    {
        String fileName = getFileName(templateName);

        File file = new File(fileName);

        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);

        if (virtualFile == null)
        {
            throw new CouldNotResolveSourceException(fileName);
        }
        else
        {
            try
            {
                return new URL(new URL("file:"), virtualFile.getUrl());
            }
            catch (MalformedURLException e)
            {
                throw new CouldNotResolveSourceException(templateName, e);
            }
        }
    }

    private String getFileName(@NotNull String templateName)
    {
        String fileName = FilenameUtils.concat(path, templateName);
        fileName = FilenameUtils.separatorsToUnix(fileName);

        return fileName;
    }

}
