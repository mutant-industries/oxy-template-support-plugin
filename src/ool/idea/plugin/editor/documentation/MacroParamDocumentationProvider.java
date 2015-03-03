package ool.idea.plugin.editor.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.javascript.JSDocTokenTypes;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.jsdoc.JSDocTag;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.TokenType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroEmptyTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.Nullable;

/**
* 1/9/15
 * TODO cely spatne
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class MacroParamDocumentationProvider extends AbstractDocumentationProvider
{
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement)
    {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement)
    {
        if(isMacroParamNamePosition(originalElement) && element instanceof PsiDocToken)
        {
            return element.getText();
        }
        else if(element != null && element.getNode().getElementType() == JSDocTokenTypes.DOC_COMMENT_DATA)
        {
            String commentText = element.getParent().getParent().getText().replaceAll("^\\s+\\*", "");

            Pattern pattern = Pattern.compile("\\s+@param(.*?)\\s+" + element.getText() + "\\s+(.*)");
            Matcher matcher = pattern.matcher(commentText);
            String resilt = "";

            if(matcher.find())
            {
                if(matcher.group(1).length() > 0)
                {
                    resilt += matcher.group(1) + ": ";
                }

                return resilt + matcher.group(2);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element)
    {
        if(isMacroParamNamePosition(element))
        {
            MacroCall macroCall = PsiTreeUtil.getParentOfType(element, MacroCall.class);
            MacroName macroName;

            if(macroCall == null || (macroName = macroCall.getMacroName()) == null
                    || macroName.getReference() == null)
            {
                return null;
            }

            String param = ((String) object).replace("=\"\"", "");
            PsiElement reference = macroName.getReference().resolve();

            if(reference instanceof PsiClass)
            {
                return generateJavaMacroParamDocumentation((PsiClass) reference, param);
            }
            else if(reference instanceof JSProperty)
            {
                return generateJsMacroParamDocumentation((JSProperty)reference, param);
            }

        }

        return null;
    }

    private boolean isMacroParamNamePosition(PsiElement element)
    {
        return element.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME
                || element.getNode().getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END
                || element.getNode().getElementType() == OxyTemplateTypes.T_XML_EMPTY_TAG_END
                || element.getNode().getElementType() == TokenType.WHITE_SPACE && (
                element.getPrevSibling() instanceof MacroAttribute
                        || element.getPrevSibling() instanceof MacroName
                        // idea 13 fix
                        || (element.getPrevSibling() instanceof PsiErrorElement && element.getPrevSibling().getPrevSibling() instanceof MacroEmptyTag)
        );
    }

    private static PsiElement generateJavaMacroParamDocumentation(PsiClass psiClass, String paramName)
    {
        PsiDocComment docComment = psiClass.getDocComment();

        if(docComment == null)
        {
            return null;
        }

        for(PsiDocTag tag : docComment.findTagsByName("param"))
        {
            PsiElement[] dataElements = tag.getDataElements();

            if(dataElements.length < 2)
            {
                continue;
            }

            String parameter = dataElements[0].getText();

            if(parameter.equals(paramName))
            {
                return dataElements[1];
            }
        }

        return null;
    }

    private static PsiElement generateJsMacroParamDocumentation(JSProperty property, String paramName)
    {
        JSDocComment comment;

        if((comment = PsiTreeUtil.getChildOfType(property, JSDocComment.class)) == null)
        {
            return null;
        }

        for(JSDocTag tag : comment.getTags())
        {
            PsiElement commentData;

            if(tag.getName() != null && tag.getName().equals("param") && (commentData = tag.getDocCommentData()) != null
                    && commentData.getText().equals(paramName))
            {
                return commentData;
            }
        }

        return null;
    }

}
