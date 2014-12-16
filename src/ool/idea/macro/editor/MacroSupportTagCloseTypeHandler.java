package ool.idea.macro.editor;

import com.intellij.codeInsight.editorActions.XmlGtTypedHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.file.MacroSupportFileViewProvider;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO end of file type fix, maybe reimplement as completion contributor
 *
 * Created by mayrp on 12/15/14.
 */
public class MacroSupportTagCloseTypeHandler extends XmlGtTypedHandler
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
                || offset < 2)
        {
            return Result.CONTINUE;
        }

        String previousChars = editor.getDocument().getText(new TextRange(offset - 1, offset));
        String macroTagToBeClosedName;

        PsiElement elementAt = provider.findElementAt(offset, MacroSupport.INSTANCE);

        if ("</".equals(editor.getDocument().getText(new TextRange(offset - 2, offset)))
            && ! TokenSet.create(MacroSupportTypes.TEMPLATE_JAVASCRIPT_CODE).contains(elementAt.getNode().getElementType())
            && (macroTagToBeClosedName = getMacroToBeClosedName(elementAt/*, provider.findElementAt(offset, StdLanguages.HTML)*/)) != null)
        {
            // <m:foo.bar> ... </ -> <m:foo.bar> ... </m:foo.bar>

            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            editor.getDocument().insertString(offset, "m:" + macroTagToBeClosedName + ">");
            editor.getCaretModel().moveToOffset(offset + macroTagToBeClosedName.length() + 3);
        }
        else if (("/".equals(previousChars)) && isOpenMacroEnd(elementAt))
        {
            // <m:foo.bar / -> <m:foo.bar />

            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            editor.getDocument().insertString(offset, ">");
            editor.getCaretModel().moveToOffset(offset + 1);
        }
        else if ((">".equals(previousChars)) && isOpenMacroEnd(elementAt))
        {
            // <m:foo.bar> -> <m:foo.bar></m:foo.bar>

            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            editor.getDocument().insertString(offset, "</m:" + getMacroName(elementAt) + ">");
        }


        return Result.CONTINUE;
    }

    /**
     * <m:foo.bar [param="value"] _
     *
     * @param elementAt
     * @return
     */
    private static boolean isOpenMacroEnd(@Nullable final PsiElement elementAt)
    {
        PsiElement psiElement = elementAt;

        if(psiElement == null)
        {
            return false;
        }

        while(true)
        {
            psiElement = psiElement.getPrevSibling();
            IElementType elementType;

            if(psiElement == null)
            {
                break;
            }

            elementType = psiElement.getNode().getElementType();

            if(elementType == MacroSupportTypes.BLOCK_STATEMENT
                    || elementType == MacroSupportTypes.TEMPLATE_HTML_CODE)
            {
                break;
            }
            else if(elementType == MacroSupportTypes.MACRO_ATTRIBUTE)
            {
                return true;
            }
            else if(elementType == MacroSupportTypes.MACRO_NAME)
            {
                if(psiElement.getPrevSibling().getPrevSibling().getNode().getElementType() == MacroSupportTypes.XML_TAG_START)
                {
                    return true;
                }

                break;
            }
        }

        return false;
    }

    /**
     * <m:foo.bar [param="value"] _ -> "foo.bar"
     *
     * @param macroBodyElement
     * @return
     */
    @Nullable
    private static String getMacroName(@Nullable final PsiElement macroBodyElement)
    {
        PsiElement psiElement = macroBodyElement;

        if(psiElement == null)
        {
            return null;
        }

        while(true)
        {
            psiElement = psiElement.getPrevSibling();
            IElementType elementType;

            if(psiElement == null)
            {
                break;
            }

            elementType = psiElement.getNode().getElementType();

            if(elementType == MacroSupportTypes.BLOCK_STATEMENT
                    || elementType == MacroSupportTypes.TEMPLATE_HTML_CODE)
            {
                break;
            }
            else if(elementType == MacroSupportTypes.MACRO_NAME)
            {
                if(psiElement.getPrevSibling().getPrevSibling().getNode().getElementType() == MacroSupportTypes.XML_TAG_START)
                {
                    return psiElement.getText();
                }

                break;
            }
        }

        return null;
    }

    @Nullable
    private static String getMacroToBeClosedName(final PsiElement macroPsiElement/*, PsiElement htmlPsiElement*/)
    {
        // macro tag has always priority over html tag: bug or feature?
        return getPreviousUnclosedMacroTagName(macroPsiElement);
    }

    @Nullable
    private static String getPreviousUnclosedMacroTagName(@Nullable final PsiElement elementAt)
    {
        PsiElement psiElement = elementAt;

        if(psiElement != null)
        {
            while((psiElement = psiElement.getPrevSibling()) != null)
            {
                if(psiElement.getNode().getElementType() == MacroSupportTypes.MACRO_NAME)
                {
                    String name = psiElement.getText();

                    psiElement = elementAt;

                    while((psiElement = psiElement.getNextSibling()) != null)
                    {
                        if(psiElement.getNode().getElementType() == MacroSupportTypes.MACRO_NAME)
                        {
                            if(psiElement.getText().equals(name))
                            {
                                return null;
                            }
                            else
                            {
                                return name;
                            }
                        }
                    }

                    return name;
                }
            }
        }

        return null;
    }

}
