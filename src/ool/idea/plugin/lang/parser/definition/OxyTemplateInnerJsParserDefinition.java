package ool.idea.plugin.lang.parser.definition;

import com.intellij.lang.javascript.nashorn.NashornJSParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.psi.tree.IFileElementType;
import ool.idea.plugin.lang.OxyTemplateInnerJs;

/**
* 1/12/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class OxyTemplateInnerJsParserDefinition extends NashornJSParserDefinition
{
    public static final IFileElementType FILE = JSFileElementType.create(OxyTemplateInnerJs.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

}
