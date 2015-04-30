package ool.idea.plugin.psi.macro.param.descriptor;

import com.intellij.lang.javascript.JSDocTokenTypes;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.jsdoc.JSDocTag;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.reference.innerjs.InnerJsTypeEvaluator;
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

    private List<String> types;

    private String defaultValue;

    private String docText;

    private boolean required;

    public JsMacroParamDescriptor(@NotNull String name, @NotNull JSProperty macro, @Nullable JSDocTag docTag, boolean usedInCode)
    {
        super(name, macro);
        JSFunctionExpression expression = PsiTreeUtil.getChildOfType(macro, JSFunctionExpression.class);

        assert expression != null;

        this.usedInCode = usedInCode;
        this.docTag = docTag;

        if (docTag != null)
        {
            types = parseTypes(docTag);
            defaultValue = parseDefaultValue(docTag);
            docText = parseDocText(docTag);
            required = parseRequired(docTag);
        }
        else
        {
            types = Collections.EMPTY_LIST;
        }
    }

    @Nullable
    @Override
    public String generateDoc()
    {
        if (docTag == null)
        {
            return null;
        }

        return super.generateDoc();
    }

    @NotNull
    @Override
    public String getMacroInfo()
    {
        String macroFqn = OxyTemplateIndexUtil.getMacroFullyQualifienName(macro);

        assert macroFqn != null;

        return macroFqn;
    }

    @Nullable
    @Override
    public String getType()
    {
        return types.size() == 0 ? null : StringUtils.join(types, "|");
    }

    @Nullable
    @Override
    public String getPrintableType()
    {
        if (types.size() == 0)
        {
            return null;
        }

        List<String> modifiedTypes = new LinkedList<>();

        for (String type : types)
        {
            // TODO duplicated code --------------------------------------------
            boolean isCollection = type.endsWith("[]");
            type = type.replaceFirst("\\s*(\\[\\])?$", "");
            // -----------------------------------------------------------------

            modifiedTypes.add((isJavaType(type) ? getDocumentationLink(type) : type) + (isCollection ? "[]" : ""));
        }

        return StringUtils.join(modifiedTypes, "|");
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

    // -------------------------------------------------------------------------------------------------

    @NotNull
    public static List<String> parseTypes(@NotNull JSDocTag docTag)
    {
        List<String> parsedTypes;
        String simpleTypeName;

        if (docTag.getValue() != null && StringUtils.isNotEmpty(simpleTypeName = docTag.getValue()
                .getText().replaceFirst("^\\{", "").replaceFirst("\\}$", "")))
        {
            // TODO tohle
            if ((parsedTypes = InnerJsTypeEvaluator.parseJavaSimplifiedRawType(simpleTypeName, docTag.getProject())).size() > 0)
            {
                return parsedTypes;
            }

            return new SmartList<>(simpleTypeName);
        }

        return Collections.EMPTY_LIST;
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
