package ool.intellij.plugin.lang.parser.definition;

import ool.intellij.plugin.lang.CompiledPreview;

import com.intellij.lang.javascript.nashorn.NashornJSParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewParserDefinition extends NashornJSParserDefinition
{
    private static final IFileElementType FILE = JSFileElementType.create(CompiledPreview.INSTANCE);

    @NotNull
    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

}
