package ool.idea.macro.editor;

import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.templateLanguages.MultipleLangCommentProvider;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.file.MacroSupportFileViewProvider;
import org.jetbrains.annotations.Nullable;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportCommentProvider implements MultipleLangCommentProvider
{
    @Nullable
    @Override
    public Commenter getLineCommenter(PsiFile file, Editor editor, Language lineStartLanguage, Language lineEndLanguage)
    {
        if (lineStartLanguage == lineEndLanguage)
        {
            return LanguageCommenters.INSTANCE.forLanguage(lineStartLanguage != MacroSupport.INSTANCE ? lineStartLanguage : MacroSupport.INSTANCE);
        }

        return null;
    }

    @Override
    public boolean canProcess(PsiFile file, FileViewProvider viewProvider)
    {
        return viewProvider instanceof MacroSupportFileViewProvider;
    }

}
