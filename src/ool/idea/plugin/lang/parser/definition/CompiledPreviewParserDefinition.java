package ool.idea.plugin.lang.parser.definition;

import com.intellij.lang.javascript.nashorn.NashornJSParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.psi.tree.IFileElementType;
import ool.idea.plugin.lang.CompiledPreview;

/**
* 2/16/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class CompiledPreviewParserDefinition extends NashornJSParserDefinition
{
    public static final IFileElementType FILE = JSFileElementType.create(CompiledPreview.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

}
