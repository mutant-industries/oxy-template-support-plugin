package ool.idea.plugin.psi.macro.param.provider;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.jsdoc.JSDocTag;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.LinkedList;
import java.util.List;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.idea.plugin.psi.macro.param.descriptor.JsMacroParamDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroParamSuggestionProvider extends ParamSuggestionProvider<JSProperty>
{
    private List<PsiElement> submergedCalls;

    private final JSFunctionExpression functionExpression;

    public JsMacroParamSuggestionProvider(@NotNull JSProperty macro)
    {
        super(macro);

        this.functionExpression = (JSFunctionExpression) macro.getLastChild();
    }

    /**
     * @return null if {@link JsMacroParamSuggestionProvider#compute()} hasn't been called yet
     */
    @Nullable
    public List<PsiElement> getSubmergedCalls()
    {
        return submergedCalls;
    }

    @NotNull
    @Override
    protected MacroParamSuggestionSet getMacroParamSuggestions()
    {
        JSParameter[] params;

        if ((params = functionExpression.getParameters()).length == 0)
        {
            return MacroParamSuggestionSet.empty();
        }

        final MacroParamSuggestionSet result = new MacroParamSuggestionSet();
        submergedCalls = new LinkedList<>();
        String paramsObjectName = params[0].getText();

        for (PsiReference reference : ReferencesSearch.search(params[0]).findAll())
        {
            PsiElement element = reference.getElement();

            if ( ! (element instanceof JSReferenceExpression))
            {
                continue;
            }

            if ((element = element.getNextSibling()) != null)
            {
                if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
                {
                    element = element.getNextSibling();
                }
                if (element != null && element.getNode().getElementType() == JSTokenTypes.DOT)
                {
                    if ((element = element.getNextSibling()) != null)
                    {
                        if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
                        {
                            element = element.getNextSibling();
                        }

                        if (element != null && element.getNode().getElementType() == JSTokenTypes.IDENTIFIER)
                        {
                            result.add(new JsMacroParamDescriptor(element.getText(), macro, 
                                    getJsParamDoc(element.getText(), macro), true));
                        }
                    }

                    continue;
                }
            }

            JSCallExpression macroCall = getMacroCall(reference.getElement());
            JSReferenceExpression callReference;

            if (macroCall == null || (callReference = PsiTreeUtil.getChildOfType(macroCall, JSReferenceExpression.class)) == null
                    || (element = callReference.resolve()) == null)
            {
                continue;
            }

            submergedCalls.add(element);
        }

        if (macro.getFirstChild() instanceof JSDocComment)
        {
            String paramName;

            for (JSDocTag docTag : ((JSDocComment) macro.getFirstChild()).getTags())
            {
                if ((paramName = getCommentParameterName(docTag)) == null)
                {
                    continue;
                }

                paramName = paramName.replaceFirst("^" + paramsObjectName + "\\.", "");

                if (result.getByName(paramName) != null)
                {
                    continue;
                }

                result.add(new JsMacroParamDescriptor(paramName, macro, docTag, false));
            }
        }

        return result;
    }

    /**
     * @param argument argument of call expression
     * @return parent call expression, where utils namespace is spipped e.g.:
     * oxy.foo(params) -> oxy.foo
     * oxy.bar(utils.extend(params, {param: false}) -> oxy.bar
     */
    @Nullable
    private static JSCallExpression getMacroCall(@Nullable PsiElement argument)
    {
        if (argument == null)
        {
            return null;
        }

        JSCallExpression callExpression = PsiTreeUtil.getParentOfType(argument, JSCallExpression.class);

        if (callExpression == null)
        {
            return null;
        }

        if (callExpression.getText().startsWith(MacroIndex.UTILS_NAMESPACE))
        {
            return getMacroCall(callExpression);
        }

        JSExpression[] arguments = callExpression.getArguments();

        if (arguments.length == 0 || ! arguments[0].isEquivalentTo(argument))
        {
            return null;
        }

        return callExpression;
    }

    @Nullable
    private static JSDocTag getJsParamDoc(@NotNull String paramName, @NotNull JSProperty macro)
    {
        JSFunctionExpression functionExpression = (JSFunctionExpression) macro.getLastChild();
        JSDocComment docComment;
        JSParameter[] referenceParams;

        if ( ! (macro.getFirstChild() instanceof JSDocComment) ||
                (referenceParams = functionExpression.getParameters()).length == 0)
        {
            return null;
        }

        String docParamExpectedName = referenceParams[0].getText() + '.' + paramName;

        docComment = (JSDocComment) macro.getFirstChild();
        String commentParameterName;

        for (JSDocTag docTag : docComment.getTags())
        {
            if ((commentParameterName = getCommentParameterName(docTag)) == null)
            {
                continue;
            }

            if (paramName.equals(commentParameterName) || docParamExpectedName.equals(commentParameterName))
            {
                return docTag;
            }
        }

        return null;
    }

    @Nullable
    private static String getCommentParameterName(@NotNull JSDocTag docTag)
    {
        PsiElement docCommentData;

        if ((docCommentData = docTag.getDocCommentData()) == null)
        {
            return null;
        }

        String commentParameterName = docCommentData.getText();

        if (commentParameterName.charAt(0) == '[' && commentParameterName.charAt(commentParameterName.length() - 1) == ']')
        {
            commentParameterName = commentParameterName.substring(1, commentParameterName.length() - 1);
        }

        return commentParameterName.replaceFirst("=.+$", "");
    }

}
