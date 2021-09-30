package ool.intellij.plugin.psi.macro.param.descriptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;

import com.intellij.lang.javascript.JSDocTokenTypes;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.jsdoc.JSDocTag;
import com.intellij.lang.javascript.psi.types.JSTypeParser;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroParamDescriptor extends MacroParamDescriptor<JSProperty>
{
    private static final Pattern OPTIONAL_VALUE_PATTERN = Pattern.compile("^[\\.a-zA-Z_]+=(.+)$");

    private final JSDocTag docTag;

    private final boolean usedInCode;

    private JSType type;

    private String defaultValue;

    private String docText;

    private boolean required = true;

    public JsMacroParamDescriptor(@NotNull String name, @NotNull JSProperty macro, @Nullable JSDocTag docTag, boolean usedInCode)
    {
        super(name, macro);

        this.usedInCode = usedInCode;
        this.docTag = docTag;

        if (docTag != null)
        {
            type = parseType(docTag);
            defaultValue = parseDefaultValue(docTag);
            docText = parseDocText(docTag);
            required = parseRequired(docTag);
        }
    }

    @Nullable
    @Override
    public JSType getType()
    {
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
        return false;
    }

    @Override
    public boolean isUsedInCode()
    {
        return usedInCode;
    }

    @Override
    public boolean isDocumented()
    {
        return docTag != null;
    }

    @NotNull
    @Override
    protected String getMacroInfo()
    {
        return OxyTemplateIndexUtil.getMacroFullyQualifiedName(macro);
    }

    // -------------------------------------------------------------------------------------------------

    @Nullable
    private static JSType parseType(@NotNull JSDocTag docTag)
    {
        String simpleTypeName;

        if (docTag.getValue() != null && StringUtils.isNotEmpty(simpleTypeName = docTag.getValue()
                .getText().replaceFirst("^\\{", "").replaceFirst("\\}$", "")))
        {
            return JSTypeParser.createType(simpleTypeName, JSTypeSource.EXPLICITLY_DECLARED);
        }

        return null;
    }

    @Nullable
    private static String parseDefaultValue(@NotNull JSDocTag docTag)
    {
        if (docTag.getDocCommentData() == null)
        {
            return null;
        }
        else
        {
            String text = docTag.getDocCommentData().getText();

            if (text.charAt(0) == '[' && text.charAt(text.length() - 1) == ']')
            {
                text = text.substring(1, text.length() - 1);
            }

            Matcher matcher = OPTIONAL_VALUE_PATTERN.matcher(text);

            if (matcher.matches())
            {
                return matcher.group(1);
            }
            else
            {
                return null;
            }
        }
    }

    @Nullable
    private static String parseDocText(@NotNull JSDocTag docTag)
    {
        StringBuilder docTextLocal = new StringBuilder();

        PsiElement nextSibling = docTag;

        while ((nextSibling = nextSibling.getNextSibling()) != null && nextSibling.getNode()
                .getElementType() != JSDocTokenTypes.DOC_COMMENT_END && ! (nextSibling instanceof JSDocTag))
        {
            if (nextSibling.getNode().getElementType() == JSDocTokenTypes.DOC_COMMENT_LEADING_ASTERISK)
            {
                continue;
            }

            docTextLocal.append(nextSibling.getText());
        }

        String result = docTextLocal.toString();

        if ((result = result.trim()).length() == 0)
        {
            return null;
        }
        else
        {
            return result.replaceFirst("^\\-\\s*", "");
        }
    }

    private static boolean parseRequired(@NotNull JSDocTag tag)
    {
        return tag.getDocCommentData() != null
                && ! tag.getDocCommentData().getText().matches("^\\[.+\\]$") && parseDefaultValue(tag) == null;
    }

}
