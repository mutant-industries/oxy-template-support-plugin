package ool.idea.plugin.psi.macro.param.descriptor;

import com.intellij.psi.PsiElement;
import java.util.regex.Pattern;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.I18nSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO refactor - getType, getPrintableType, isJavaType
 *
 * 4/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroParamDescriptor<T extends PsiElement>
{
    protected static final Pattern JAVA_CLASS_FQN_PATTERN = Pattern.compile("([a-z][a-z_0-9]*\\.)*[A-Z_]($[A-Z_]|[\\w_])*");;

    protected final String name;

    protected final T macro;

    public MacroParamDescriptor(@NotNull String name, @NotNull T macro)
    {
        assert OxyTemplateIndexUtil.isMacro(macro);

        this.name = name;
        this.macro = macro;
    }

    @Nullable
    abstract public String getType();

    @Nullable
    abstract public String getPrintableType();

    @Nullable
    abstract public String getDefaultValue();

    @Nullable
    abstract public String getDocText();

    @NotNull
    abstract public String getMacroInfo();

    abstract public boolean isRequired();

    abstract public boolean isNotNull();

    abstract public boolean isUsedInCode();

    abstract public boolean isDocumented();

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public T getMacro()
    {
        return macro;
    }

    public static boolean isJavaType(String type)
    {
        return JAVA_CLASS_FQN_PATTERN.matcher(type).matches();
    }

    @Nullable
    public String generateDoc()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("<h5>");

        if (getType() != null || ! isRequired() || isNotNull())
        {
            builder.append("[ ");

            if (getType() != null)
            {
                builder.append(getPrintableType());
            }
            if ( ! isRequired())
            {
                builder.append(builder.length() > 6 ? ", " : "").append(I18nSupport.message("macro.param.optional"));

                if (getDefaultValue() != null)
                {
                    builder.append(", ").append(I18nSupport.message("macro.param.default")).append(": ").append(getDefaultValue());
                }
            }
            if (isNotNull())
            {
                builder.append(builder.length() > 6 ? ", " : "").append("notnull");
            }

            builder.append(" ] ");
        }

        builder.append("in ").append(getMacroInfo());

        builder.append("</h5>");

        if (getDocText() != null)
        {
            builder.append("<p>").append(getDocText()).append("</p>");
        }

        String result = builder.toString().trim();

        if (result.length() == 0)
        {
            return null;
        }

        return result;
    }

    // -------------------------------------------------------------------------------------------------
    @NonNls
    protected static String getDocumentationLink(@NotNull String fullyQualifiedName)
    {
        return getDocumentationLink(fullyQualifiedName, fullyQualifiedName.replaceFirst("^.+\\.", ""));
    }

    @NonNls
    @NotNull
    protected static String getDocumentationLink(@NotNull String fullyQualifiedName, @NotNull String text)
    {
        return "<b><a href=\"psi_element://" + fullyQualifiedName + "\"><code>" +
                text + "</code></a></b>";
    }

}
