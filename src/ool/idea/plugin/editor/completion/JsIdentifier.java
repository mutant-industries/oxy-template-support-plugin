package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import ool.idea.plugin.file.index.JavaMacroNameIndex;
import ool.idea.plugin.file.index.JsGlobalsIndex;
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
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(JSTokenTypes.IDENTIFIER),
            new CompletionProvider<CompletionParameters>()
            {

                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    PsiElement psiElement = parameters.getPosition().getPrevSibling();

                    if(psiElement != null && psiElement.getNode().getElementType() == JSTokenTypes.DOT)
                    {
                        if((psiElement = psiElement.getPrevSibling()) != null && psiElement.getNode().getElementType() == JSElementTypes.REFERENCE_EXPRESSION
                                && "oxy".equals(psiElement.getText()) && psiElement.getPrevSibling() == null)
                        {
                            // oxy namespace
                            for(String key : FileBasedIndex.getInstance().getAllKeys(JavaMacroNameIndex.INDEX_ID, parameters.getPosition().getProject()))
                            {
                                resultSet.addElement(LookupElementBuilder.create(key).withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                            }
                        }
                    }
                    else
                    {
                        //  global vars
                        for(String key : FileBasedIndex.getInstance().getAllKeys(JsGlobalsIndex.INDEX_ID, parameters.getPosition().getProject()))
                        {
                            resultSet.addElement(LookupElementBuilder.create(key).withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
                        }
                    }
                }
            }
        );
    }

}
