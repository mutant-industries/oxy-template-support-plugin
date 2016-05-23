package ool.intellij.plugin.editor.documentation;

import com.intellij.lang.javascript.documentation.JSDocumentationProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/20/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
class JSDocPlainCommentBuilder implements JSDocumentationProcessor
{
    private final StringBuilder builder;

    public JSDocPlainCommentBuilder()
    {
        builder = new StringBuilder();
    }

    @Override
    public boolean needsPlainCommentData()
    {
        return true;
    }

    @Override
    public boolean onCommentLine(@NotNull String line)
    {
        builder.append(line);

        return true;
    }

    @Override
    public boolean onPatternMatch(@NotNull MetaDocType metaDocType, @Nullable String s, @Nullable String s1, @Nullable String s2, @NotNull String s3, @NotNull String s4)
    {
        return true;
    }

    @Override
    public void postProcess()
    {

    }

    @NotNull
    public String getDoc()
    {
        return builder.toString();
    }

}

