package ool.idea.macro.file;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.PsiFileEx;
import javax.swing.Icon;
import ool.idea.macro.MacroSupport;
import org.jetbrains.annotations.NotNull;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportFile extends PsiFileBase implements PsiFileEx
{
    public MacroSupportFile(@NotNull FileViewProvider viewProvider)
    {
        super(viewProvider, MacroSupport.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType()
    {
        return MacroSupportFileType.INSTANCE;
    }

    @Override
    public String toString()
    {
        return "Oxy macro";
    }

    @Override
    public Icon getIcon(int flags)
    {
        return super.getIcon(flags);
    }

}
