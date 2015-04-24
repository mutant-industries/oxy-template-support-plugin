package ool.idea.plugin.psi.macro.param;

import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValuesManager;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.macro.param.provider.JavaMacroParamSuggestionProvider;
import ool.idea.plugin.psi.macro.param.provider.JsMacroParamSuggestionProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 4/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroParamHelper
{
    private static final Key<CachedValue<MacroParamSuggestionSet>> MACRO_SUGGESTION_PARAM_SET_KEY =
            Key.create("MACRO_SUGGESTION_PARAM_SET_KEY");

    @NotNull
    public static MacroParamSuggestionSet getMacroParamSuggestions(@NotNull PsiElement macro)
    {
        if ( ! OxyTemplateIndexUtil.isMacro(macro))
        {
            return MacroParamSuggestionSet.empty();
        }

        if (macro instanceof PsiClass)
        {
            return getJavaMacroParamSuggestions((PsiClass) macro);
        }
        else if (macro instanceof JSProperty)
        {
            return getJsMacroParamSuggestions((JSProperty) macro);
        }

        return MacroParamSuggestionSet.empty();
    }

    // --------------------- java macros -------------------------------------------------

    @NotNull
    public static MacroParamSuggestionSet getJavaMacroParamSuggestions(@NotNull PsiClass psiClass)
    {
        if ( ! OxyTemplateIndexUtil.isMacro(psiClass))
        {
            return MacroParamSuggestionSet.empty();
        }

        CachedValue<MacroParamSuggestionSet> cached = psiClass.getUserData(MACRO_SUGGESTION_PARAM_SET_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(psiClass.getProject())
                    .createCachedValue(new JavaMacroParamSuggestionProvider(psiClass), false);

            psiClass.putUserData(MACRO_SUGGESTION_PARAM_SET_KEY, cached);
        }

        return cached.getValue();
    }

    // --------------------- JS macros ---------------------------------------------------

    @NotNull
    public static MacroParamSuggestionSet getJsMacroParamSuggestions(@NotNull JSProperty macro)
    {
        return getJsMacroParamSuggestions(macro, true);
    }

    /**
     * @param macro
     * @param searchDeep add suggestions of all macros where params object is passed
     * @return all attribute names queried on params object (first parameter) in js function
     */
    @NotNull
    public static MacroParamSuggestionSet getJsMacroParamSuggestions(@NotNull JSProperty macro,
                                                                     boolean searchDeep)
    {
        final MacroParamSuggestionSet result = new MacroParamSuggestionSet();

        if ( ! OxyTemplateIndexUtil.isMacro(macro))
        {
            return result;
        }

        CachedValue<MacroParamSuggestionSet> cached = macro.getUserData(MACRO_SUGGESTION_PARAM_SET_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(macro.getProject())
                    .createCachedValue(new JsMacroParamSuggestionProvider(macro), false);

            macro.putUserData(MACRO_SUGGESTION_PARAM_SET_KEY, cached);
        }

        result.addAll(cached.getValue());

        if (searchDeep)
        {
            List<PsiElement> submergedCalls = ((JsMacroParamSuggestionProvider) cached.getValueProvider())
                    .getSubmergedCalls();

            assert submergedCalls != null;

            for (PsiElement submergedCall : submergedCalls)
            {
                result.addAll(getMacroParamSuggestions(submergedCall));
            }
        }

        return result;
    }

}
