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

    private String result;

    public FileContentReader(VirtualFile file)
    {
        this.file = file;
    }

    @Override
    public void run()
    {
        Document document = FileDocumentManager.getInstance().getDocument(file);

        assert document != null;
        result = document.getText();
    }

    public String getResult()
    {
        return result;
    }

}
