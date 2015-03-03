package ool.idea.plugin.lang.parser;

import com.intellij.lang.javascript.JavascriptParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IFileElementType;
import ool.idea.plugin.lang.CompiledPreview;
import ool.idea.plugin.lang.lexer.OxyTemplateInnerJsLexerAdapter;
import org.jetbrains.annotations.NotNull;

/**
* 2/16/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class CompiledPreviewParserDefinition extends JavascriptParserDefinition
{
    public static final IFileElementType FILE = new JSFileElementType(CompiledPreview.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project)
    {
        return new OxyTemplateInnerJsLexerAdapter();
    }

}
