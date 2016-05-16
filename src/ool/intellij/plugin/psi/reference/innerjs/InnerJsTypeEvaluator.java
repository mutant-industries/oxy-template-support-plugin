package ool.intellij.plugin.psi.reference.innerjs;

import java.util.LinkedList;
import java.util.List;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;
import ool.intellij.plugin.psi.MacroAttribute;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.MacroParam;
import ool.intellij.plugin.psi.macro.param.MacroParamHelper;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;

import com.google.common.collect.ImmutableList;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSTypeEvaluator;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.JSVarStatement;
import com.intellij.lang.javascript.psi.JSVariable;
import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.resolve.JSTypeProcessor;
import com.intellij.lang.javascript.psi.types.JSArrayTypeImpl;
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/23/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsTypeEvaluator extends NashornJSTypeEvaluator
{
    public InnerJsTypeEvaluator(JSEvaluateContext context, JSTypeProcessor processor)
    {
        super(context, processor);
    }

    @Override
    protected void addTypeFromVariableResolveResult(@NotNull JSVariable jsVariable)
    {
        JSProperty macro;

        // macro first parameter
        if (jsVariable instanceof JSParameter && (macro = checkMacroFirstParameter((JSParameter) jsVariable)) != null)
        {
            addType(getMacroFirstParameterType(macro), jsVariable);

            return;
        }
        // function parameter
        else if (jsVariable instanceof JSParameter && jsVariable.getType() != null)
        {
            jsVariable.getType().accept(new SimplifiedClassNameResolver(myContext.targetFile));
        }

        super.addTypeFromVariableResolveResult(jsVariable);
    }

    @Override
    protected boolean addTypeFromResolveResult(@NotNull PsiElement resolveResult, boolean hasSomeType)
    {
        JSType type;

        // oxy.repeat
        if ((type = checkForEachDefinition(resolveResult)) != null)
        {
            addType(type, resolveResult);

            return true;
        }
        // globals
        else if (resolveResult instanceof GlobalVariableDefinition)
        {
            addType(((GlobalVariableDefinition) resolveResult).getType(), resolveResult);

            return true;
        }

        return super.addTypeFromResolveResult(resolveResult, hasSomeType);
    }


    // TODO temp code, see https://youtrack.jetbrains.com/issue/WEB-16383
    @Override
    protected JSExpression evaluateCallExpressionTypes(JSCallExpression callExpression)
    {
        PsiElement resolve;

        if (callExpression.getMethodExpression() instanceof JSReferenceExpression
                && (resolve = ((JSReferenceExpression) callExpression.getMethodExpression()).resolve()) instanceof PsiMethod)
        {
            addType(InnerJsJavaTypeConverter.getPsiElementJsType(resolve), callExpression);

            return callExpression;
        }

        return super.evaluateCallExpressionTypes(callExpression);
    }
    // -------------------------------------------------------------------

    @NotNull
    private JSType getMacroFirstParameterType(@NotNull JSProperty macro)
    {
        assert OxyTemplateIndexUtil.isMacro(macro);

        JSTypeSource typeSource = JSTypeSourceFactory.createTypeSource(myContext.targetFile, true);
        List<JSRecordTypeImpl.TypeMember> members = new LinkedList<>();

        for (MacroParamDescriptor paramDescriptor : MacroParamHelper.getJsMacroParamSuggestions(macro, false))
        {
            if ( ! paramDescriptor.isDocumented())
            {
                continue;
            }

            JSRecordTypeImpl.PropertySignature signature = new JSRecordTypeImpl.PropertySignatureImpl(paramDescriptor.getName(),
                    paramDescriptor.getType(), ! paramDescriptor.isRequired());

            members.add(signature);
        }

        JSRecordTypeImpl type = new JSRecordTypeImpl(typeSource, ImmutableList.copyOf(members));
        type.accept(new SimplifiedClassNameResolver(myContext.targetFile));

        return type;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    private static JSType checkForEachDefinition(@NotNull final PsiElement element)
    {
        PsiElement elementLocal = element;

        if (elementLocal.getParent() instanceof JSVarStatement)
        {
            elementLocal = elementLocal.getParent();
        }

        // repeat macro - var keyword is missing
        if (elementLocal instanceof PsiPackage || ! (elementLocal.getFirstChild() instanceof JSVariable))
        {
            return null;
        }

        PsiElement elementAt = elementLocal.getContainingFile().getViewProvider()
                .findElementAt(elementLocal.getNode().getStartOffset(), OxyTemplate.INSTANCE);

        assert elementAt != null;

        MacroAttribute attribute = PsiTreeUtil.getParentOfType(elementAt, MacroAttribute.class);

        if (attribute == null || ! MacroIndex.REPEAT_MACRO_VARIABLE_DEFINITION.equals(attribute.getMacroParamName().getText()))
        {
            return null;
        }

        MacroCall macroCall = PsiTreeUtil.getParentOfType(attribute, MacroCall.class);

        assert macroCall != null;

        for (MacroAttribute macroAttribute : macroCall.getMacroAttributeList())
        {
            if (MacroIndex.REPEAT_MACRO_LIST_DEFINITION.equals(macroAttribute.getMacroParamName().getText()))
            {
                MacroParam macroParam;

                if ((macroParam = macroAttribute.getMacroParam()) == null)
                {
                    return null;
                }

                PsiElement list = elementLocal.getContainingFile().getViewProvider().findElementAt(macroParam.getNode().getStartOffset()
                        + macroParam.getTextLength() - 1, OxyTemplateInnerJs.INSTANCE);

                JSReferenceExpression statement = PsiTreeUtil.getParentOfType(list, JSReferenceExpression.class);

                if (statement != null)
                {
                    BaseJSSymbolProcessor.SimpleTypeProcessor typeProcessor = new BaseJSSymbolProcessor.SimpleTypeProcessor();
                    JSTypeEvaluator.evaluateTypes(statement, statement.getContainingFile(), typeProcessor);

                    if (typeProcessor.getType() instanceof JSArrayTypeImpl)
                    {
                        return ((JSArrayTypeImpl) typeProcessor.getType()).getType();
                    }
                }
            }
        }

        return null;
    }

    /**
     * @param parameter
     * @return macro, if parameter is its first parameter, null otherwise
     */
    @Nullable
    private static JSProperty checkMacroFirstParameter(@Nullable JSParameter parameter)
    {
        JSProperty macro;
        JSFunctionExpression functionExpression;

        if ((macro = PsiTreeUtil.getParentOfType(parameter, JSProperty.class)) != null
                && OxyTemplateIndexUtil.isMacro(macro) && macro.getLastChild() instanceof JSFunctionExpression)
        {
            functionExpression = (JSFunctionExpression) macro.getLastChild();

            if (functionExpression.getParameters().length > 0 && functionExpression.getParameters()[0].getSource()
                    .isEquivalentTo(parameter.getSource()))
            {
                return macro;
            }
        }

        return null;
    }

}
