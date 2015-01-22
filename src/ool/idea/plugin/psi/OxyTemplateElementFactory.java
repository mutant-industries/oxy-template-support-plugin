package ool.idea.plugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.file.OxyTemplateFileType;
import org.jetbrains.annotations.NotNull;

/**
 * 1/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateElementFactory
{
    @NotNull
    public static MacroNameIdentifier createMacroNameIdentifier(@NotNull Project project, String name)
    {
        return createMacroName(project, name).getMacroNameIdentifierList().get(0);
    }

    @NotNull
    public static MacroName createMacroName(@NotNull Project project, String name)
    {
        return  createMacroUnpairedTag(project, name).getMacroName();
    }

    @NotNull
    public static MacroUnpairedTag createMacroUnpairedTag(@NotNull Project project, String name)
    {
        return  ((MacroUnpairedTag) createFile(project, "<m:" + name + " />").getFirstChild());
    }

    @NotNull
    public static DirectiveStatement createDirectiveStatement(@NotNull Project project, String directive, String... params)
    {
        StringBuilder builder = new StringBuilder("<%@ " + directive);

        for(String param : params)
        {
            builder.append(" \"" + param + "\" ");
        }

        builder.append("%>");

        return (DirectiveStatement) createFile(project, builder.toString()).getFirstChild();
    }

    @NotNull
    public static OxyTemplateFile createFile(@NotNull Project project, String text)
    {
        return (OxyTemplateFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.jsm", OxyTemplateFileType.INSTANCE, text);
    }



}
