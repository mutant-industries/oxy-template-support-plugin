package ool.idea.plugin.file;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.PsiFileEx;
import javax.swing.Icon;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import ool.idea.plugin.lang.OxyTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFile extends PsiFileBase implements PsiFileEx
{
    public OxyTemplateFile(@NotNull FileViewProvider viewProvider)
    {
        super(viewProvider, OxyTemplate.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType()
    {
        return OxyTemplateFileType.INSTANCE;
    }

    @Override
    public String toString()
    {
        return "Oxy template";
    }

    @Override
    public Icon getIcon(int flags)
    {
        return super.getIcon(flags);
    }

}
