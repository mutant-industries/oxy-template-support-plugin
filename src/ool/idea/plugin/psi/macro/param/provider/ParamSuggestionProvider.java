package ool.idea.plugin.psi.macro.param.provider;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider;
import java.util.HashSet;
import java.util.Set;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.idea.plugin.psi.macro.param.descriptor.MacroParamDescriptor;
import ool.idea.plugin.psi.reference.innerjs.SimplifiedClassNameResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/24/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class ParamSuggestionProvider<T extends PsiElement> implements CachedValueProvider<MacroParamSuggestionSet>
{
    protected final T macro;

    protected final Set<PsiElement> cacheDependencies;

    public ParamSuggestionProvider(@NotNull T macro)
    {
        assert OxyTemplateIndexUtil.isMacro(macro);

        this.macro = macro;
        this.cacheDependencies = new HashSet<>(10);

        cacheDependencies.add(macro);
    }

    @Nullable
    @Override
    public Result<MacroParamSuggestionSet> compute()
    {
        MacroParamSuggestionSet macroParamSuggestions = getMacroParamSuggestions();

        Set<PsiElement> cacheDependencies = new HashSet<>();

        for (MacroParamDescriptor descriptor : macroParamSuggestions)
        {
            if (descriptor.getType() == null)
            {
                continue;
            }

            SimplifiedClassNameResolver simplifiedClassNameResolver = new SimplifiedClassNameResolver(macro.getContainingFile());
            descriptor.getType().accept(simplifiedClassNameResolver);

            for (PsiClass aClass : simplifiedClassNameResolver.getResolvedClassList())
            {
                cacheDependencies.add(aClass);
            }
        }

        cacheDependencies.addAll(this.cacheDependencies);

        return Result.create(macroParamSuggestions, cacheDependencies);
    }

    abstract protected MacroParamSuggestionSet getMacroParamSuggestions();

}
