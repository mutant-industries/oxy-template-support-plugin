package ool.idea.plugin.editor.completion.macro;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import java.util.List;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.ExpressionStatement;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.OxyTemplateTypes;
import ool.idea.plugin.psi.macro.param.descriptor.MacroParamDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroParamName extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("\"\\w+=\"");

    public XmlMacroParamName()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_PARAM_NAME).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {
                @Override
                public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    MacroCall macroCall = PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroCall.class);

                    assert macroCall != null;

                    List<MacroAttribute> attributes = macroCall.getMacroAttributeList();

                    main:
                    for (MacroParamDescriptor paramDescriptor : macroCall.getParamSuggestionSet())
                    {
                        if ( ! paramDescriptor.isUsedInCode())
                        {
                            continue;
                        }

                        for (MacroAttribute attribute : attributes)
                        {
                            if (paramDescriptor.getName().equals(attribute.getMacroParamName().getText()))
                            {
                                continue main;  // don't suggest already used params
                            }
                        }

                        String typeText = paramDescriptor.getType() != null ? paramDescriptor.getType().replaceFirst("^.+\\.", "") : null;
                        StringBuilder lookupStringBuilder = new StringBuilder(paramDescriptor.getName() + "=\"");

                        if(paramDescriptor.getType() != null && ! paramDescriptor.getType().equals(String.class.getName())
                                && ! paramDescriptor.getType().equalsIgnoreCase(String.class.getSimpleName()))
                        {
                            lookupStringBuilder.append(ExpressionStatement.EXPRESSION_PREFIX).append(" ");
                        }

                        lookupStringBuilder.append("\"");

                        resultSet.consume(LookupElementBuilder.create(paramDescriptor, lookupStringBuilder.toString())
                            .withPresentableText(paramDescriptor.getName())
                            .withTypeText(typeText, true)
                            .withBoldness(paramDescriptor.isRequired())
                            .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                            {
                                @Override
                                public void handleInsert(InsertionContext context, LookupElement item)
                                {
                                    CaretModel caretModel = context.getEditor().getCaretModel();
                                    caretModel.moveToOffset(caretModel.getOffset() - 1);

                                    super.handleInsert(context, item);
                                }
                            })
                        );
                    }
                }
            }
        );
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
        if (typeChar == ' ')
        {
            IElementType elementType = position.getNode().getElementType();

            if (elementType == OxyTemplateTypes.T_MACRO_NAME || elementType == OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY
                    && position.getParent().getLastChild().isEquivalentTo(position))
            {
                return true;
            }
        }

        return false;
    }

}
