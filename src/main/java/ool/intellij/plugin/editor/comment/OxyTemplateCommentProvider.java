package ool.intellij.plugin.editor.comment;

import ool.intellij.plugin.file.OxyTemplateFileViewProvider;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;

import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.templateLanguages.MultipleLangCommentProvider;
import org.jetbrains.annotations.Nullable;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateCommentProvider implements MultipleLangCommentProvider
{
    @Nullable
    @Override
    public Commenter getLineCommenter(PsiFile file, Editor editor, Language lineStartLanguage, Language lineEndLanguage)
    {
        if (lineStartLanguage == lineEndLanguage && lineStartLanguage == OxyTemplateInnerJs.INSTANCE)
        {
            return LanguageCommenters.INSTANCE.forLanguage(OxyTemplateInnerJs.INSTANCE);
        }

        return LanguageCommenters.INSTANCE.forLanguage(OxyTemplate.INSTANCE);
    }

    @Override
    public boolean canProcess(PsiFile file, FileViewProvider viewProvider)
    {
        return viewProvider instanceof OxyTemplateFileViewProvider;
    }

}
