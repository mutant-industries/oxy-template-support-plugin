package ool.intellij.plugin.editor.completion.macro;

import java.util.List;
import java.util.regex.Pattern;

import ool.intellij.plugin.editor.completion.ExpressionStatement;
import ool.intellij.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.psi.MacroAttribute;
import ool.intellij.plugin.psi.MacroCall;
import ool.intellij.plugin.psi.OxyTemplateTypes;
import ool.intellij.plugin.psi.macro.param.descriptor.MacroParamDescriptor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.psi.types.primitives.JSStringType;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
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
                public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    PsiElement elementAt = parameters.getOriginalFile().getViewProvider().findElementAt(parameters.getOffset() - 1, OxyTemplate.INSTANCE);

                    MacroCall macroCall = elementAt instanceof PsiWhiteSpace && elementAt.getPrevSibling() instanceof MacroCall ?
                            (MacroCall) elementAt.getPrevSibling() : PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), MacroCall.class);

                    if (macroCall == null)
                    {
                        return;
                    }

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

                        StringBuilder lookupStringBuilder = new StringBuilder(paramDescriptor.getName() + "=\"");

                        if (paramDescriptor.getType() != null && ! (paramDescriptor.getType() instanceof JSStringType))
                        {
                            lookupStringBuilder.append(ExpressionStatement.EXPRESSION_PREFIX).append(" ");
                        }

                        lookupStringBuilder.append("\"");

                        resultSet.consume(LookupElementBuilder.create(paramDescriptor, lookupStringBuilder.toString())
                            .withPresentableText(paramDescriptor.getName())
                            .withTypeText(paramDescriptor.getPrintableType(), true)
                            .withBoldness(paramDescriptor.isDocumented() && paramDescriptor.isRequired())
                            .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                            {
                                @Override
                                public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
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

}
