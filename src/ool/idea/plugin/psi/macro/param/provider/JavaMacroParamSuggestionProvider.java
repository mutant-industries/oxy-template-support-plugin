package ool.idea.plugin.psi.macro.param.provider;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.idea.plugin.psi.macro.param.descriptor.JavaMacroParamDescriptor;
import ool.web.template.MacroEvent;
import ool.web.template.MacroParameterHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroParamSuggestionProvider extends ParamSuggestionProvider<PsiClass>
{
    public static final String MACRO_EVENT_FQN = MacroEvent.class.getName();

    public static final String MACRO_PARAM_HELPER_FQN = MacroParameterHelper.class.getName();

    public JavaMacroParamSuggestionProvider(@NotNull PsiClass macro)
    {
        super(macro);
    }

    @NotNull
    @Override
    protected MacroParamSuggestionSet getMacroParamSuggestions()
    {
        MacroParamSuggestionSet params = new MacroParamSuggestionSet();

        PsiMethod[] methods = macro.findMethodsByName("execute", true);
        PsiMethod executeMethod;
        PsiParameter eventParam;

        // current and / or superclass(es) + interface method
        if (methods.length < 2 || (executeMethod = methods[0]).getParameterList().getParametersCount() < 1
                || (eventParam = executeMethod.getParameterList().getParameters()[0]) == null || ! eventParam.getType().equalsToText(MACRO_EVENT_FQN))
        {
            return params;
        }

        for (PsiReference reference : ReferencesSearch.search(eventParam).findAll())
        {
            PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(reference.getElement(), PsiLocalVariable.class);

            if (localVariable == null || ! localVariable.getType().equalsToText(MACRO_PARAM_HELPER_FQN))
            {
                continue;
            }
            // TODO the case when object is passed to another method

            params.addAll(getParamsPulledFromParamHelper(localVariable));
        }

        // event.getParams().get(...) - not used, therefore not supported

        return params;
    }

    private MacroParamSuggestionSet getParamsPulledFromParamHelper(PsiLocalVariable paramHelper)
    {
        MacroParamSuggestionSet params = new MacroParamSuggestionSet();
        PsiReferenceExpression methodReference;

        for (PsiReference reference : ReferencesSearch.search(paramHelper).findAll())
        {
            if ((methodReference = PsiTreeUtil.getParentOfType(reference.getElement(), PsiReferenceExpression.class)) != null)
            {
                String parameterName = null;
                boolean required = false;
                boolean notNull = true;
                String type = Object.class.getName();
                String defaultValue = null;

                while (methodReference != null)
                {
                    if ( ! (methodReference.getParent() instanceof PsiMethodCallExpression)
                            || methodReference.getReferenceName() == null)
                    {
                        break;
                    }

                    PsiMethodCallExpression callExpression = (PsiMethodCallExpression) methodReference.getParent();
                    PsiExpression[] expressions = callExpression.getArgumentList().getExpressions();

                    if (expressions.length == 0)
                    {
                        break;
                    }

                    switch (methodReference.getReferenceName())
                    {
                        case "parameter":
                        case "pullParameter":
                            parameterName = getExpressionValue(expressions[0]);

                            if (expressions.length == 2 && expressions[1].getFirstChild() instanceof PsiTypeElement)
                            {
                                type = ((PsiTypeElement) expressions[1].getFirstChild()).getType().getCanonicalText(false);
                            }

                            break;

                        case "pullIntegerValue":

                            parameterName = getExpressionValue(expressions[0]);

                            notNull = expressions.length < 2;
                            required = expressions.length < 2;
                            type = Integer.class.getName();

                            if (expressions.length == 2)
                            {
                                defaultValue = getExpressionValue(expressions[1]);
                            }

                            break;

                        case "pullBooleanValue":

                            parameterName = getExpressionValue(expressions[0]);

                            notNull = false;
                            required = false;
                            type = Boolean.class.getName();

                            if (expressions.length == 2)
                            {
                                defaultValue = getExpressionValue(expressions[1]);
                            }

                            break;

                        case "setNonNull":
                            notNull = Boolean.valueOf(expressions[0].getText());
                            break;

                        case "setRequired":
                            required = Boolean.valueOf(expressions[0].getText());
                            break;
                    }

                    methodReference = PsiTreeUtil.getParentOfType(methodReference, PsiReferenceExpression.class);
                }

                if (parameterName != null)
                {
                    params.add(new JavaMacroParamDescriptor(parameterName, macro, notNull, required, type, defaultValue));
                }
            }
            // TODO the case when object is passed to another method
        }

        return params;
    }

    @Nullable
    private static String getExpressionValue(@NotNull PsiExpression expression)
    {
        Object value = JavaConstantExpressionEvaluator.computeConstantExpression(expression, false);

        if (value != null)
        {
            return value.toString();
        }

        return null;
    }

}
