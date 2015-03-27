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
import ool.idea.plugin.file.OxyTemplateFileViewProvider;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroEmptyTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 12/15/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ParamQuoteHandler extends TypedHandlerDelegate
{
    @Override
    public Result beforeCharTyped(char c, Project project, @NotNull Editor editor, @NotNull PsiFile file, FileType fileType)
    {
        FileViewProvider provider = file.getViewProvider();
        int offset;

        if (!(provider instanceof OxyTemplateFileViewProvider) || (offset = editor.getCaretModel().getOffset()) < 1)
        {
            return Result.CONTINUE;
        }

        PsiElement elementAt;

        if (c == '"')
        {
            // <%@ layout "_ %> -> <%@ layout "_" %>, <m:foo.bar param="_ -> <m:foo.bar param="_"
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            if((elementAt = provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)) == null)
            {
                return Result.CONTINUE;
            }

            if(OxyTemplateParserDefinition.WHITE_SPACES.contains(elementAt.getNode().getElementType()))
            {
                elementAt = elementAt.getPrevSibling();

                if(elementAt instanceof MacroEmptyTag)
                {
                    elementAt = elementAt.getLastChild();
                }
                if(elementAt instanceof MacroAttribute)
                {
                    elementAt = elementAt.getLastChild();

                    if(elementAt instanceof PsiErrorElement)
                    {
                        elementAt = elementAt.getPrevSibling();
                    }
                }
                else if(elementAt instanceof DirectiveStatement)
                {
                    if(((DirectiveStatement)elementAt).getDirectiveParamWrapperList().size() == 0)
                    {
                        editor.getDocument().insertString(offset, "\"");
                    }
                }
                else if(elementAt instanceof PsiErrorElement
                        && elementAt.getPrevSibling().getNode().getElementType() == OxyTemplateTypes.T_DIRECTIVE)
                {
                    editor.getDocument().insertString(offset, "\"");
                }
            }

            if(elementAt.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_ASSIGNMENT)
            {
                editor.getDocument().insertString(offset, "\"");
            }
            else if(elementAt.getNode().getElementType() == OxyTemplateTypes.T_DIRECTIVE
                    && elementAt.getNextSibling().getNode().getStartOffset() == offset)
            {
                editor.getDocument().insertString(offset, "\"");
            }
        }
        else if (c == '=')
        {
            // <m:foo.bar param=_ -> <m:foo.bar param="_"
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

            if((elementAt = provider.findElementAt(offset - 1, OxyTemplate.INSTANCE)) == null)
            {
                return Result.CONTINUE;
            }

            if(elementAt.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME
                    && elementAt.getNextSibling() == null
                    && (elementAt.getNode().getStartOffset() + elementAt.getTextLength()) == offset)
            {
                editor.getDocument().insertString(offset, "=\"\"");
                editor.getCaretModel().moveToOffset(offset + 2);

                return Result.STOP;
            }
        }

        return Result.CONTINUE;
    }

}
