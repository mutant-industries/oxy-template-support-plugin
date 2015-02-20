package ool.idea.plugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.file.OxyTemplateFileType;
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
        return  createMacroUnpairedTag(project, name).getMacroName();
    }

    @NotNull
    public static MacroParamName createMacroParamName(@NotNull Project project, String name)
    {
        MacroAttribute attribute = createMacroAttribute(project, name, "dummy");

        return attribute.getMacroParamName();
    }

    @NotNull
    public static MacroAttribute createMacroAttribute(@NotNull Project project, String name, String value)
    {
        MacroUnpairedTag tag = createMacroUnpairedTag(project, "dummy", name + "\"" + value + "\"");

        return tag.getMacroAttributeList().get(0);
    }

    @NotNull
    public static MacroUnpairedTag createMacroUnpairedTag(@NotNull Project project, String name, String... attributes)
    {
        StringBuilder attributeListBulder = new StringBuilder();

        for(String attribute : attributes)
        {
            attributeListBulder.append(attribute + " ");
        }

        return  ((MacroUnpairedTag) createFile(project, "<m:" + name + " " + attributeListBulder + "/>").getFirstChild());
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
