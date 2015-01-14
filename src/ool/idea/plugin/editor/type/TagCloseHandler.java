package ool.idea.plugin.editor.type;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.file.OxyTemplateParserDefinition;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.MacroUnpairedTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class TagCloseHandler extends TypedHandlerDelegate
{
    @Override
    public Result beforeCharTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file, FileType fileType)
    {
        FileViewProvider provider = file.getViewProvider();
        PsiElement elementAt;

        if (!(provider instanceof OxyTemplateFileViewProvider))
        {
            return super.beforeCharTyped(c, project, editor, file, fileType);
        }

        int offset = editor.getCaretModel().getOffset();

        if (offset > editor.getDocument().getTextLength()
                || offset < 4)
        {
            return Result.CONTINUE;
        }

        if (c == '>' && isOpenMacroEnd(provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)))
        {
            // <m:foo.bar> -> <m:foo.bar></m:foo.bar>
            String macroTagToBeClosedName;

            editor.getDocument().insertString(offset, ">");
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            elementAt = provider.findElementAt(offset, OxyTemplate.INSTANCE);

            if((macroTagToBeClosedName = getPreviousUnclosedMacroTagName(elementAt)) != null)
            {
                String closeTag = "</m:" + macroTagToBeClosedName + ">";
                editor.getDocument().insertString(offset + 1, closeTag);
            }

            editor.getCaretModel().moveToOffset(offset + 1);

            return Result.STOP;
        }
        else if (c == '/')
        {
            // <m:foo.bar / -> <m:foo.bar />

            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            if(isOpenMacroEnd(provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)))
            {
                editor.getDocument().insertString(offset, "/>");
                editor.getCaretModel().moveToOffset(offset + 2);

                return Result.STOP;
            }
        }

        return Result.CONTINUE;
    }

    /**
     * <m:foo.bar [param="value"] _
     *
     * @param elementAt
     * @return
     */
    @Nullable
    private static boolean isOpenMacroEnd(@Nullable final PsiElement elementAt)
    {
        if(elementAt == null)
        {
            return false;
        }

        PsiElement psiElement;

        if(OxyTemplateParserDefinition.WHITE_SPACES.contains(elementAt.getNode().getElementType()))
        {
            psiElement = elementAt.getPrevSibling();

            if(psiElement instanceof MacroUnpairedTag
                    && psiElement.getLastChild().getNode().getElementType() != OxyTemplateTypes.T_XML_UNPAIRED_TAG_END)
            {
                return true;
            }
        }
        else if(elementAt.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME)
        {
            if((psiElement = PsiTreeUtil.getParentOfType(elementAt, MacroUnpairedTag.class)) != null
                    && psiElement.getLastChild() instanceof PsiErrorElement
                    && elementAt.getParent().isEquivalentTo(psiElement.getLastChild().getPrevSibling()))
            {
                return true;
            }
        }
        else if(elementAt.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY)
        {
            if(elementAt.getParent().getLastChild().isEquivalentTo(elementAt)
                    && (psiElement = PsiTreeUtil.getParentOfType(elementAt, MacroUnpairedTag.class)) != null)
            {
                if(psiElement.getLastChild() instanceof PsiErrorElement
                        && psiElement.getLastChild().getPrevSibling().isEquivalentTo(elementAt.getParent()))
                {
                    return true;
                }
                else if(psiElement.getLastChild() instanceof MacroAttribute // parser fix
                        && psiElement.getLastChild().isEquivalentTo(elementAt.getParent()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public static String getPreviousUnclosedMacroTagName(@Nullable final PsiElement elementAt)
    {
        if(elementAt == null)
        {
            return null;
        }

        PsiElement psiElement = elementAt;

        do
        {
            if(psiElement instanceof MacroName)
            {
                String name = psiElement.getText();

                psiElement = elementAt;

                while((psiElement = psiElement.getNextSibling()) != null)
                {
                    if(psiElement instanceof MacroName)
                    {
                        if(psiElement.getText().equals(name))
                        {
                            if((psiElement = PsiTreeUtil.getTopmostParentOfType(elementAt, MacroTag.class)) != null
                                    && ((MacroTag) psiElement).getMacroNameList().size() == 1)
                            {
                                return name;
                            }

                            return null;
                        }

                        return name;
                    }
                }

                return name;
            }
        }
        while((psiElement = psiElement.getPrevSibling()) != null);

        return null;
    }

}
