package ool.intellij.plugin.lang.parser.definition;

import ool.intellij.plugin.lang.OxyTemplateInnerJs;

import com.intellij.lang.javascript.nashorn.NashornJSParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.psi.tree.IFileElementType;

/**
 * 1/12/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsParserDefinition extends NashornJSParserDefinition
{
    private static final IFileElementType FILE = JSFileElementType.create(OxyTemplateInnerJs.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

}
