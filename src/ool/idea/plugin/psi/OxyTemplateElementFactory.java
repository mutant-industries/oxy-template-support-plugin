package ool.idea.plugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateElementFactory
{
    @NotNull
    public static MacroName createMacroName(@NotNull Project project, String name)
    {
        return  createMacroEmptyTag(project, name).getMacroName();
    }

    @NotNull
    public static MacroEmptyTag createMacroEmptyTag(@NotNull Project project, String name)
    {
        return  ((MacroEmptyTag) createFile(project, "<m:" + name + " />").getFirstChild());
    }

    @NotNull
    public static DirectiveStatement createDirectiveStatement(@NotNull Project project, @NonNls String directive, String... params)
    {
        StringBuilder builder = new StringBuilder("<%@ " + directive);

        for(String param : params)
        {
            builder.append(" \"").append(param).append("\" ");
        }

        builder.append("%>");

        return (DirectiveStatement) createFile(project, builder.toString()).getFirstChild();
    }

    @NotNull
    public static OxyTemplateFile createFile(@NotNull Project project, @NonNls String text)
    {
        return (OxyTemplateFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.jsm", OxyTemplateFileType.INSTANCE, text);
    }

}
