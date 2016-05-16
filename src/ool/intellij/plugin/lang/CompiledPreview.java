package ool.intellij.plugin.lang;

import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.JSLanguageDialect;

/**
 * 2/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreview extends JSLanguageDialect
{
    public static final CompiledPreview INSTANCE = new CompiledPreview();

    protected CompiledPreview()
    {
        super("CompiledPreview", DialectOptionHolder.OTHER);
    }

    @Override
    public String getFileExtension()
    {
        return "js";
    }

}
