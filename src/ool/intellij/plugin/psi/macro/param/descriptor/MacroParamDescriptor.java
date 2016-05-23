package ool.intellij.plugin.psi.macro.param.descriptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.lang.javascript.psi.JSType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
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
    abstract public JSType getType();

    @Nullable
    abstract public String getDefaultValue();

    @Nullable
    abstract public String getDocText();

    abstract public boolean isRequired();

    abstract public boolean isNotNull();

    abstract public boolean isUsedInCode();

    abstract public boolean isDocumented();

    @NotNull
    abstract protected String getMacroInfo();

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

    @Nullable
    public String getPrintableType()
    {
        return getPrintableType(false);
    }

    @Nullable
    protected String getPrintableType(boolean generateLinks)
    {
        if (getType() == null)
        {
            return null;
        }

        String typeText = getType().getTypeText();
        Matcher matcher = JAVA_CLASS_FQN_PATTERN.matcher(typeText);
        StringBuilder result = new StringBuilder();
        int position = 0;

        while (matcher.find())
        {
            result.append(typeText.substring(position, matcher.start()));
            String javaClassFqn = typeText.substring(matcher.start(), matcher.end());

            result.append(generateLinks ? getDocumentationLink(javaClassFqn)
                    : javaClassFqn.replaceFirst("^.+\\.", ""));

            position = matcher.end();
        }

        result.append(typeText.substring(position));

        return result.toString();
    }

    @Nullable
    public String generateDoc()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("<h5>")
                .append(generateTypeInfo())
                .append("in ").append(getMacroInfo())
                .append("</h5>");

        if (getDocText() != null)
        {
            builder.append("<p>")
                    .append(getDocText())
                    .append("</p>");
        }

        String result = builder.toString().trim();

        if (result.length() == 0)
        {
            return null;
        }

        return result;
    }

    public String generateTypeInfo()
    {
        StringBuilder builder = new StringBuilder();

        if (getType() != null || !isRequired() || isNotNull())
        {
            builder.append("[ ");

            if (getType() != null)
            {
                builder.append(getPrintableType(true));
            }
            if ( ! isRequired())
            {
                builder.append(builder.length() > 2 ? ", " : "").append(I18nSupport.message("macro.param.optional"));

                if (getDefaultValue() != null)
                {
                    builder.append(", ").append(I18nSupport.message("macro.param.default")).append(": ").append(getDefaultValue());
                }
            }
            if (isNotNull())
            {
                builder.append(builder.length() > 2 ? ", " : "").append("notnull");
            }

            builder.append(" ] ");
        }

        return builder.toString();
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
