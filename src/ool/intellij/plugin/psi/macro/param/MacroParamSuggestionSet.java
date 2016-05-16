package ool.intellij.plugin.psi.macro.param;

import java.util.HashSet;

import ool.intellij.plugin.psi.macro.param.descriptor.JavaMacroParamDescriptor;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroParamSuggestionSet extends HashSet<MacroParamDescriptor>
{
    @Override
    public boolean add(@NotNull MacroParamDescriptor macroParamDescriptor)
    {
        for (MacroParamDescriptor descriptor : this)
        {
            if (macroParamDescriptor.getName().equals(descriptor.getName()))
            {
                /**
                 * the same parameter can be commented on several places - choose the most informative descriptor
                 */
                if (macroParamDescriptor instanceof JavaMacroParamDescriptor
                        || descriptor.isRequired() == macroParamDescriptor.isRequired() &&
                            (descriptor.getDocText() == null && macroParamDescriptor.getDocText() != null
                            || descriptor.getType() == null && macroParamDescriptor.getType() != null
                            || descriptor.getDefaultValue() == null && macroParamDescriptor.getDefaultValue() != null))
                {
                    remove(descriptor);

                    return add(macroParamDescriptor);
                }

                return false;
            }
        }

        return super.add(macroParamDescriptor);
    }

    @Nullable
    public MacroParamDescriptor getByName(@Nullable String name)
    {
        if (name == null)
        {
            return null;
        }

        for (MacroParamDescriptor descriptor : this)
        {
            if (name.equals(descriptor.getName()))
            {
                return descriptor;
            }
        }

        return null;
    }

    @NotNull
    public static MacroParamSuggestionSet empty()
    {
        return new MacroParamSuggestionSet();
    }

}
