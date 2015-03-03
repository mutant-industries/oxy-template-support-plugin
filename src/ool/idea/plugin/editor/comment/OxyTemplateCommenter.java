package ool.idea.plugin.editor.comment;

import com.intellij.lang.Commenter;

/**
 * 2/27/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateCommenter implements Commenter
{
    @Override
    public String getLineCommentPrefix()
    {
        return null;
    }

    @Override
    public String getBlockCommentPrefix()
    {
        return "<//";
    }

    @Override
    public String getBlockCommentSuffix()
    {
        return "//>";
    }

    @Override
    public String getCommentedBlockCommentPrefix()
    {
        return null;
    }

    @Override
    public String getCommentedBlockCommentSuffix()
    {
        return null;
    }

}
