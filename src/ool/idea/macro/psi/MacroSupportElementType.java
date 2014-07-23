package ool.idea.macro.psi;

import com.intellij.psi.tree.IElementType;
import ool.idea.macro.MacroSupport;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportElementType extends IElementType {

    public MacroSupportElementType(@NotNull @NonNls String debugName) {
        super(debugName, MacroSupport.getInstance());
    }

}
