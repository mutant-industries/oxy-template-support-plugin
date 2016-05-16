package ool.intellij.plugin.editor.documentation;

import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.OxyTemplateTypes;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
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
        if (contextElement != null && contextElement.getNode().getElementType() == OxyTemplateTypes.T_MACRO_PARAM_NAME)
        {
            return contextElement;
        }

        return null;
    }

}
