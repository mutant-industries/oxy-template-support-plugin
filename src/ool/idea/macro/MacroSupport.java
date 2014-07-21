package ool.idea.macro;

import com.intellij.lang.Language;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupport  extends Language
{
    private static final MacroSupport INSTANCE = new MacroSupport();

    private MacroSupport()
    {
        super("macroSupport");
    }

    public static MacroSupport getInstance()
    {
        return INSTANCE;
    }

}
