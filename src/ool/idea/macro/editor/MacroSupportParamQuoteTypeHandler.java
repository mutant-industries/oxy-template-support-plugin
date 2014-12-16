package ool.idea.macro.editor;

import com.intellij.codeInsight.editorActions.XmlGtTypedHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.file.MacroSupportFileViewProvider;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mayrp on 12/15/14.
 */
public class MacroSupportParamQuoteTypeHandler extends XmlGtTypedHandler
{
    @Override
    public Result charTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file)
    {
        FileViewProvider provider = file.getViewProvider();

        if (!(provider instanceof MacroSupportFileViewProvider))
        {
            return super.charTyped(c, project, editor, file);
        }

        int offset = editor.getCaretModel().getOffset();

        if (offset > editor.getDocument().getTextLength()
                || offset < 1)
        {
            return Result.CONTINUE;
        }

        String previousChars = editor.getDocument().getText(new TextRange(offset - 1, offset));

        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        PsiElement elementAt = provider.findElementAt(offset, MacroSupport.INSTANCE);
        PsiElement psiElement;

        if ("\"".equals(previousChars) && elementAt != null)
        {
            // <%@ layout "_ %> -> <%@ layout "_" %>
            if((psiElement = elementAt.getPrevSibling()) != null
                    && psiElement.getNode().getElementType() == MacroSupportTypes.DIRECTIVE_PARAM_BOUNDARY
                    && (psiElement = psiElement.getPrevSibling()) != null
                    && psiElement.getNode().getElementType() != MacroSupportTypes.DIRECTIVE_PARAM_BOUNDARY)
            {
                editor.getDocument().insertString(offset, "\"");
            }
        }
        else if ("=".equals(previousChars)  && elementAt != null)
        {
            // <m:foo.bar param=_ -> <m:foo.bar param="_"
            if((psiElement = elementAt.getPrevSibling()) != null
                    && psiElement.getNode().getElementType() == MacroSupportTypes.MACRO_PARAM_ASSIGNMENT
                    && (psiElement = psiElement.getPrevSibling()) != null
                    && psiElement.getNode().getElementType() == MacroSupportTypes.MACRO_PARAM_NAME)
            {
                editor.getDocument().insertString(offset, "\"\"");
                editor.getCaretModel().moveToOffset(offset + 1);
            }
        }

        return Result.CONTINUE;
    }

}
