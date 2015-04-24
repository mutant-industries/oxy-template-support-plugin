package ool.idea.plugin.psi.macro.param.descriptor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.javadoc.PsiDocComment;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroParamDescriptor extends MacroParamDescriptor<PsiClass>
{
    private final boolean required;

    private final boolean notNull;

    private final String type;

    private final String defaultValue;

    private String docText;

    public JavaMacroParamDescriptor(@NotNull String name, @NotNull PsiClass macro, boolean notNull, boolean required,
                                    @Nullable String type, @Nullable String defaultValue)
    {
        super(name, macro);

        this.notNull = notNull;
        this.required = required;
        this.type = type;
        this.defaultValue = defaultValue;

        PsiDocComment comment = macro.getDocComment();

        if (comment != null)
        {
            String commentText = comment.getText().replaceAll("\\*", "");
            Pattern parameterPattern = Pattern.compile("<li>\\s*(<.+?>)?\\s*" + name + "\\s*(</.+?>)?\\s*(-\\s*)?(.*?)</li>",
                    Pattern.DOTALL);
            Matcher matcher = parameterPattern.matcher(commentText);

            if (matcher.find() && matcher.group(4) != null)
            {
                String docText = matcher.group(4).replaceAll("\n\\s+", "\n");

                if (docText.length() > 0)
                {
                    this.docText = docText;
                }
            }
        }
    }

    @NotNull
    @Override
    public String getMacroInfo()
    {
        String macroQualifiedName = OxyTemplateIndexUtil.getMacroFullyQualifienName(macro);
        String macroClassQualifiedName = macro.getQualifiedName();

        assert macroQualifiedName != null;

        if (macroClassQualifiedName == null)
        {
            return macroQualifiedName;
        }

        return getDocumentationLink(macroClassQualifiedName, macroQualifiedName);
    }

    @Nullable
    @Override
    public String getType()
    {
        if (type == null || type.equals(Object.class.getName()))
        {
            return null;
        }

        return type;
    }

    @Nullable
    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }

    @Nullable
    @Override
    public String getDocText()
    {
        return docText;
    }

    @Override
    public boolean isRequired()
    {
        return required;
    }

    @Override
    public boolean isNotNull()
    {
        return notNull;
    }

    @Override
    public boolean isUsedInCode()
    {
        return true;
    }

}
