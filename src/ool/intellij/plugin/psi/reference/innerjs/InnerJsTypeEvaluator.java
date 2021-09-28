package ool.intellij.plugin.psi.reference.innerjs;

import java.util.LinkedList;
import java.util.List;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.psi.macro.param.MacroParamHelper;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;

import com.google.common.collect.ImmutableList;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSTypeEvaluator;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
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
    public InnerJsTypeEvaluator(@NotNull JSEvaluateContext context)
    {
        super(context);
    }

    @Override
    protected boolean addTypeFromAmdModuleReference(@NotNull JSParameter parameter)
    {
        JSProperty macro;
        JSType type;

        // macro first parameter
        if ((macro = checkMacroFirstParameter(parameter)) != null)
        {
            addType(getMacroFirstParameterType(macro));

            return true;
        }
        // function parameter
        else if ((type = parameter.getJSType()) != null)
        {
            type.accept(new SimplifiedClassNameResolver(myContext.targetFile));
        }

        return super.addTypeFromAmdModuleReference(parameter);
    }

    @Override
    protected void addTypeFromElementResolveResult(PsiElement resolveResult)
    {
        // globals
        if (resolveResult instanceof GlobalVariableDefinition)
        {
            addType(((GlobalVariableDefinition) resolveResult).getType());

            return;
        }

        super.addTypeFromElementResolveResult(resolveResult);
    }


    // TODO temp code, see https://youtrack.jetbrains.com/issue/WEB-16383
    @Override
    protected void evaluateCallExpressionTypes(@NotNull JSCallExpression callExpression)
    {
        PsiElement resolve;

        if (callExpression.getMethodExpression() instanceof JSReferenceExpression
                && (resolve = ((JSReferenceExpression) callExpression.getMethodExpression()).resolve()) instanceof PsiMethod)
        {
            addType(InnerJsJavaTypeConverter.getPsiElementJsType(resolve));

            return;
        }

        super.evaluateCallExpressionTypes(callExpression);
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
                    paramDescriptor.getType(), ! paramDescriptor.isRequired(), false);

            members.add(signature);
        }

        JSRecordTypeImpl type = new JSRecordTypeImpl(typeSource, ImmutableList.copyOf(members));
        type.accept(new SimplifiedClassNameResolver(myContext.targetFile));

        return type;
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

            if (functionExpression.getParameters().length > 0 && functionExpression.getParameters()[0]
                    .isEquivalentTo(parameter))
            {
                return macro;
            }
        }

        return null;
    }

}
