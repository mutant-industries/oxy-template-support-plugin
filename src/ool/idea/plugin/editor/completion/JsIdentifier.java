package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import static com.intellij.patterns.PlatformPatterns.not;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.editor.completion.lookupElement.JsGlobalVariableLookupElementProvider;
import ool.idea.plugin.editor.completion.lookupElement.JsMacroNameLookupElementProvider;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsIdentifier extends CompletionContributor
{
    public JsIdentifier()
    {
        extend(CompletionType.BASIC, psiElement(JSTokenTypes.IDENTIFIER).withParent(not(psiElement(JSProperty.class))),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    PsiElement psiElement = parameters.getPosition();
                    JSReferenceExpression topReference = PsiTreeUtil.getTopmostParentOfType(psiElement, JSReferenceExpression.class);

                    if(topReference != null && psiElement.getPrevSibling() != null && psiElement.getPrevSibling()
                            .getNode().getElementType() == JSTokenTypes.DOT)
                    {
                        String partialText = topReference.getText()
                                .substring(0, parameters.getPosition().getStartOffsetInParent());

                        OxyTemplateIndexUtil.addMacroNameCompletions(partialText, psiElement.getProject(), resultSet, JsMacroNameLookupElementProvider.INSTANCE);
                    }
                    else
                    {
                        //  global vars
                        OxyTemplateIndexUtil.addGlobalVariableCompletions(psiElement.getProject(), resultSet, JsGlobalVariableLookupElementProvider.INSTANCE);
                    }
                }
            }
        );
    }

}
