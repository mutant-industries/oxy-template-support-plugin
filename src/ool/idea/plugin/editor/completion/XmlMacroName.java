package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.editor.completion.lookupElement.XmlMacroNameLookupElementProvider;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroName extends CompletionContributor
{
    public XmlMacroName()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_NAME_IDENTIFIER).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    MacroName elementAt = PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroName.class);

                    String partialText = elementAt.getText().substring(0, parameters.getPosition().getParent().getStartOffsetInParent());


                    if(elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() == OxyTemplateTypes.T_XML_TAG_START)
                    {
                        OxyTemplateIndexUtil.addMacroNameCompletions(partialText, elementAt.getProject(), resultSet, XmlMacroNameLookupElementProvider.INSTANCE);
                    }
                }
            }
        );
    }

}
