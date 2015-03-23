package ool.idea.plugin.action.compile;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * 2/27/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class FileContentReader implements Runnable
{
    private final VirtualFile file;

    private CharSequence result;

    public FileContentReader(VirtualFile file)
    {
        this.file = file;
    }

    @Override
    public void run()
    {
        Document document = FileDocumentManager.getInstance().getCachedDocument(file);

        if(document == null)
        {
            document = FileDocumentManager.getInstance().getDocument(file);
        }

        assert document != null;
        result = document.getCharsSequence();
    }

    public CharSequence getResult()
    {
        return result;
    }

}
