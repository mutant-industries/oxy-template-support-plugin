package ool.intellij.plugin.editor.documentation;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.lang.I18nSupport;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.MacroName;
import ool.intellij.plugin.psi.OxyTemplateTypes;
import ool.intellij.plugin.psi.macro.param.MacroParamHelper;
import ool.intellij.plugin.psi.macro.param.MacroParamSuggestionSet;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.documentation.JSDocumentationUtils;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroParamDocumentationProvider extends AbstractDocumentationProvider
{
    private static final Key<MacroParamDescriptor> MACRO_PARAM_DESCRIPTOR_KEY = Key.create("MACRO_PARAM_DESCRIPTOR_KEY");

    @Nullable
    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement)
    {
        MacroParamDescriptor macroParamDescriptor;
        MacroCall macroCall;

        if ((macroParamDescriptor = element.getUserData(MACRO_PARAM_DESCRIPTOR_KEY)) == null && element.getNode() != null &&
                element.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME && (macroCall = PsiTreeUtil.getParentOfType(element, MacroCall.class)) != null)
        {
            macroParamDescriptor = macroCall.getParamSuggestionSet().getByName(element.getText());
        }

        if (macroParamDescriptor != null)
        {
            return macroParamDescriptor.generateDoc();
        }

        if (element instanceof FakeDocumentationPsiElement)
        {
            return generateJsMacroDocumentation(((FakeDocumentationPsiElement) element).getMacro());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element)
    {
        if (object instanceof MacroParamDescriptor)
        {
            MacroParamDescriptor descriptor = (MacroParamDescriptor) object;

            /**
             * We just need to pass the descriptor attached to some element, that will be ignored by other providers
             */
            element.putUserData(MACRO_PARAM_DESCRIPTOR_KEY, descriptor);

            return element;
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement)
    {
        PsiElement element;
        PsiReference reference;

        if (contextElement != null)
        {
            if (contextElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME)
            {
                return contextElement;
            }
            else if ((contextElement.getNode().getElementType() == JSTokenTypes.IDENTIFIER || contextElement.getNode().getElementType() == JSTokenTypes.COLON)
                    && (((element = PsiTreeUtil.getParentOfType(contextElement, JSReferenceExpression.class)) != null
                        && (element = ((JSReferenceExpression) element).resolve()) != null)
                        || (element = contextElement.getParent()) instanceof JSProperty)
                    && element instanceof JSProperty && OxyTemplateIndexUtil.isMacro(element))
            {
                return new FakeDocumentationPsiElement((JSProperty) element);
            }
            else if (contextElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME
                    && (element = PsiTreeUtil.getParentOfType(contextElement, MacroName.class)) != null
                    && (reference = element.getReference()) != null && (element = reference.resolve()) != null
                    && element instanceof JSProperty && OxyTemplateIndexUtil.isMacro(element))
            {
                return new FakeDocumentationPsiElement((JSProperty) element);
            }
        }

        return null;
    }

    private static String generateJsMacroDocumentation(@NotNull JSProperty macro)
    {
        JSDocComment comment;
        MacroParamSuggestionSet paramDescriptors = MacroParamHelper.getJsMacroParamSuggestions(macro, false);
        String qualifiedName = OxyTemplateIndexUtil.getMacroFullyQualifiedName(macro);

        assert qualifiedName != null;

        StringBuilder result = new StringBuilder();

        result.append("<PRE><b>").append(qualifiedName).append("</b>(&nbsp;[ optional ] params )\n</PRE> ");

        if ((comment = JSStubBasedPsiTreeUtil.findDocComment(macro)) != null)
        {
            JSDocPlainCommentBuilder builder = new JSDocPlainCommentBuilder();
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

            for (MacroParamDescriptor descriptor : paramDescriptors)
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

        return result.toString();
    }

}
