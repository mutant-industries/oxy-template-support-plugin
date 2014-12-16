package ool.idea.macro.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by mayrp on 12/15/14.
 */
public class MacroSupportBlockMarkerMatcher implements PairedBraceMatcher
{
    private static final BracePair[] ourBracePairs = {
            new BracePair(MacroSupportTypes.OPEN_BLOCK_MARKER, MacroSupportTypes.CLOSE_BLOCK_MARKER, true),
            new BracePair(MacroSupportTypes.OPEN_BLOCK_MARKER_PRINT, MacroSupportTypes.CLOSE_BLOCK_MARKER, true),
            new BracePair(MacroSupportTypes.OPEN_BLOCK_MARKER_DIRECTIVE, MacroSupportTypes.CLOSE_BLOCK_MARKER, true)};

    @Override
    public BracePair[] getPairs()
    {
        return ourBracePairs;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType)
    {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset)
    {
        return openingBraceOffset;
    }
}
