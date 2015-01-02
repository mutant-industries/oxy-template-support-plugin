package ool.idea.plugin.editor.comment;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateCommenter implements Commenter
{
    @Nullable
    @Override
    public String getLineCommentPrefix()
    {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix()
    {
        return "<% /*";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix()
    {
        return "*/ %>";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix()
    {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix()
    {
        return null;
    }

}
