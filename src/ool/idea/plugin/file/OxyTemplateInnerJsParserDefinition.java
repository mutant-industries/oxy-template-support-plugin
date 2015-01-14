package ool.idea.plugin.file;

import com.intellij.lang.javascript.JavascriptParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.psi.tree.IFileElementType;
import ool.idea.plugin.lang.OxyTemplateInnerJs;

/**
* 1/12/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class OxyTemplateInnerJsParserDefinition extends JavascriptParserDefinition
{
    public static final IFileElementType FILE = new JSFileElementType(OxyTemplateInnerJs.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

}
