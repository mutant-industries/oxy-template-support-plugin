package ool.idea.plugin.psi.macro.param.descriptor;

import com.intellij.lang.javascript.JSDocTokenTypes;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.jsdoc.JSDocTag;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.search.AllClassesSearchExecutor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
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

    private String type;

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
            type = parseType(docTag);
            defaultValue = parseDefaultValue(docTag);
            docText = parseDocText(docTag);
            required = parseRequired(docTag);
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

    // -------------------------------------------------------------------------------------------------

    @Nullable
    private static String parseType(@NotNull JSDocTag docTag)
    {
        String simpleTypeName;

        if(docTag.getValue() != null && StringUtils.isNotEmpty(simpleTypeName = docTag.getValue()
                .getText().replaceFirst("^\\{", "").replaceFirst("\\}$", "")))
        {
            if(Character.isUpperCase(simpleTypeName.charAt(0)) && ! simpleTypeName.contains("."))
            {
                GlobalSearchScope scope = ProjectScope.getProjectScope(docTag.getProject());
                final List<PsiClass> classes = new LinkedList<>();

                AllClassesSearchExecutor.processClassesByNames(docTag.getProject(), scope, Collections.singletonList(simpleTypeName),
                    new Processor<PsiClass>()
                    {
                        @Override
                        public boolean process(PsiClass psiClass)
                        {
                            classes.add(psiClass);

                            return true;
                        }
                    });

                if(classes.size() == 1)
                {
                    return classes.get(0).getQualifiedName();
                }
            }

            return simpleTypeName;
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
