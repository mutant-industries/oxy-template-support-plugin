package ool.idea.plugin.lang.lexer;

import com.intellij.psi.tree.IElementType;
import java.util.HashMap;
import java.util.Map;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 2/5/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractOxyTemplateLexer
{
    @NonNls
    private static final Map<CharSequence, CharSequence[]> macroAttributesContainingJavascript
            = new HashMap<CharSequence, CharSequence[]>();

    protected CharSequence lastSeenMacroName;

    protected CharSequence lastSeenAttributeName;

    static
    {
        macroAttributesContainingJavascript.put("oxy.repeat", new String[]
        {
                "varName", "indexName"
        });
    }

    /**
     * Ugly but effective
     *
     * @return
     */
    @NotNull
    protected IElementType decideParameterType()
    {
        names:
        for(Map.Entry<CharSequence, CharSequence[]> entry : macroAttributesContainingJavascript.entrySet())
        {
            CharSequence macroName = entry.getKey();

            if(macroName.length() != lastSeenMacroName.length())
            {
                continue;
            }

            for(int i = 0; i < macroName.length(); i++)
            {
                if(macroName.charAt(i) != lastSeenMacroName.charAt(i))
                {
                    continue names;
                }
            }

            attributes:
            for(CharSequence macroSttribute : entry.getValue())
            {
                if(macroSttribute.length() != lastSeenAttributeName.length())
                {
                    continue;
                }

                for(int i = 0; i < macroSttribute.length(); i++)
                {
                    if(macroSttribute.charAt(i) != lastSeenAttributeName.charAt(i))
                    {
                        continue attributes;
                    }

                    return OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE;
                }
            }
        }

        return OxyTemplateTypes.T_MACRO_PARAM;
    }

}
