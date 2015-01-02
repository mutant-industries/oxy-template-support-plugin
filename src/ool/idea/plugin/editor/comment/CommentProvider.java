package ool.idea.plugin.editor.comment;

import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.templateLanguages.MultipleLangCommentProvider;
import ool.idea.plugin.OxyTemplate;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import org.jetbrains.annotations.Nullable;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CommentProvider implements MultipleLangCommentProvider
{
    @Nullable
    @Override
    public Commenter getLineCommenter(PsiFile file, Editor editor, Language lineStartLanguage, Language lineEndLanguage)
    {
        // TODO temp code
        if (lineStartLanguage == lineEndLanguage)
        {
            return LanguageCommenters.INSTANCE.forLanguage(lineStartLanguage == JavascriptLanguage.INSTANCE ? JavascriptLanguage.INSTANCE : OxyTemplate.INSTANCE);
        }

        return null;
    }

    @Override
    public boolean canProcess(PsiFile file, FileViewProvider viewProvider)
    {
        return viewProvider instanceof OxyTemplateFileViewProvider;
    }

}
