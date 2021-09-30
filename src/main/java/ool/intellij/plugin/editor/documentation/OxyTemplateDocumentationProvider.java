package ool.intellij.plugin.editor.documentation;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.OxyTemplateTypes;
import ool.intellij.plugin.psi.macro.param.MacroParamHelper;
import ool.intellij.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.documentation.JSDocumentationProvider;
import com.intellij.lang.javascript.documentation.JSDocumentationUtils;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateDocumentationProvider extends JSDocumentationProvider
{
    @Nullable
    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement)
    {
        if (originalElement == null)
        {
            return null;
        }

        MacroCall macroCall;
        PsiElement macroName = null;
        PsiElement macro = null;
        PsiReference reference;

        // macro parameter documentation
        if (originalElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME
                && (macroCall = PsiTreeUtil.getParentOfType(originalElement, MacroCall.class)) != null)
        {
            MacroParamDescriptor<?> macroParamDescriptor = macroCall.getParamSuggestionSet().getByName(originalElement.getText());

            if (macroParamDescriptor != null)
            {
                return macroParamDescriptor.generateDoc();
            }
        }

        // macro documentation - js code
        if (originalElement.getNode().getElementType() == JSTokenTypes.IDENTIFIER
                || originalElement.getNode().getElementType() == JSTokenTypes.COLON)
        {
            // macro definition
            if ( ! ((macro = originalElement.getParent()) instanceof JSProperty))
            {
                JSReferenceExpression referenceExpression = PsiTreeUtil.getParentOfType(originalElement, JSReferenceExpression.class);

                // macro call
                if (referenceExpression != null)
                {
                    macro = ((JSReferenceExpression) macro).resolve();
                }
            }
        }

        // macro documentation - template code
        else
        {
            if (originalElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME)
            {
                macroName = PsiTreeUtil.getParentOfType(originalElement, MacroName.class);
            }
            else if (originalElement.getNode().getElementType() == TokenType.WHITE_SPACE)
            {
                macroName = originalElement.getPrevSibling();
            }

            if (macroName instanceof MacroName && (reference = macroName.getReference()) != null)
            {
                macro = reference.resolve();
            }
        }

        if (macro instanceof JSProperty && OxyTemplateIndexUtil.isMacro(macro))
        {
            return generateJsMacroDocumentation((JSProperty) macro, originalElement);
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement, int targetOffset)
    {
        if (contextElement != null && contextElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME)
        {
            return contextElement;
        }

        return null;
    }

    public String generateJsMacroDocumentation(@NotNull JSProperty macro, PsiElement originalElement)
    {
        JSDocComment comment;
        MacroParamSuggestionSet paramDescriptors = MacroParamHelper.getJsMacroParamSuggestions(macro, false);
        String qualifiedName = OxyTemplateIndexUtil.getMacroFullyQualifiedName(macro);

        assert qualifiedName != null;

        StringBuilder result = new StringBuilder();

        result.append("<PRE><b>").append(qualifiedName).append("</b>(&nbsp;[ optional ] params )\n</PRE> ");

        JSDocPlainCommentBuilder builder = null;

        if ((comment = JSStubBasedPsiTreeUtil.findDocComment(macro)) != null)
        {
            builder = new JSDocPlainCommentBuilder(comment, originalElement, comment, this);
            JSDocumentationUtils.processDocumentationTextFromComment(comment, comment.getNode(), builder);

            String doc = builder.getDoc().trim();

            if (doc.length() > 0)
            {
                result.append(doc).append("<br/>");
            }
        }

        if (paramDescriptors.size() != 0)
        {
            result.append("<br/><b>").append(I18nSupport.message("macro.param.block.heading")).append("</b><ul>");

            for (MacroParamDescriptor<?> descriptor : paramDescriptors)
            {
                result.append("<li>")
                        .append(descriptor.getName())
                        .append(" ")
                        .append(descriptor.generateTypeInfo());

                if ( ! StringUtil.isEmpty(descriptor.getDocText()))
                {
                    result.append(" - ").append(descriptor.getDocText());
                }

                result.append("</li>");
            }

            result.append("</ul>");
        }

        if (builder != null)
        {
            result.append(builder.getSeeAlso());
        }

        return result.toString();
    }

}
