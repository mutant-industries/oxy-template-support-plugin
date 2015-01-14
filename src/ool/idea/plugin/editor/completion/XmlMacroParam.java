package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.MacroUnpairedTag;
import ool.idea.plugin.psi.OxyTemplatePsiElement;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 * TODO refactoring
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroParam extends CompletionContributor
{
    private static final Pattern MACRO_PARAM_NAME_PULL = Pattern.compile(".parameter\\(\\\"([a-zA-Z_1-9]+)\\\"[^;]+pullValue\\s*\\(\\s*\\)\\s*;");
    private static final Pattern MACRO_PARAM_NAME_PULL_MAP = Pattern.compile("event\\s*.getParams\\s*\\(\\s*\\)\\s*\\.get\\s*\\(\\\"([a-zA-Z_1-9]+)\\\"\\);");

    public XmlMacroParam()
    {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(OxyTemplateTypes.T_MACRO_PARAM_NAME).withLanguage(OxyTemplate.INSTANCE),
            new CompletionProvider<CompletionParameters>()
            {

                @Override
                public void addCompletions(@NotNull CompletionParameters parameters,
                                           ProcessingContext context,
                                           @NotNull CompletionResultSet resultSet)
                {
                    // TODO psi refactoring
                    OxyTemplatePsiElement parent = PsiTreeUtil.getNonStrictParentOfType(parameters.getPosition(), MacroUnpairedTag.class, MacroTag.class);
                    List<MacroAttribute> attributes = Collections.emptyList();

                    MacroName macroName = null;

                    if(parent instanceof MacroUnpairedTag)
                    {
                        macroName = ((MacroUnpairedTag)parent).getMacroName();
                        attributes = ((MacroUnpairedTag)parent).getMacroAttributeList();
                    }
                    else if(parent instanceof MacroTag)
                    {
                        macroName = ((MacroTag)parent).getMacroNameList().get(0);
                        attributes = ((MacroTag)parent).getMacroAttributeList();
                    }

                    if(macroName == null || macroName.getReference() == null)
                    {
                        return;
                    }
                    // ---------------

                    PsiElement reference = macroName.getReference().resolve();

                    if(reference == null || reference.getParent() == null
                            || ! (reference.getParent() instanceof PsiClass))
                    {
                        return;
                    }

                    PsiClass psiClass = (PsiClass)reference.getParent();

                    // TODO find better way to do that
                    PsiMethod[] methods = psiClass.findMethodsByName("execute", false);

                    if(methods.length == 0)
                    {
                        return;
                    }

                    PsiMethod executeMethod = methods[0];

                    if(executeMethod.getBody() == null)
                    {
                        return;
                    }

                    String methodContent = executeMethod.getBody().getText();

                    Matcher matcher;
                    ArrayList<String> params = new ArrayList<String>();

                    matcher = MACRO_PARAM_NAME_PULL.matcher(methodContent);

                    while(matcher.find())
                    {
                        params.add(matcher.group(1));
                    }

                    matcher = MACRO_PARAM_NAME_PULL_MAP.matcher(methodContent);

                    while(matcher.find())
                    {
                        params.add(matcher.group(1));
                    }

                    main:
                    for(String param : params)
                    {
                        for(MacroAttribute attribute : attributes)
                        {
                            if(param.equals(attribute.getMacroParamName().getText()))
                            {
                                continue main;  // don't report already used params
                            }
                        }

                        resultSet.addElement(LookupElementBuilder.create(param + "=\"\"").withPresentableText(param)
                            .withInsertHandler(new InsertHandler<LookupElement>()
                            {
                                @Override
                                public void handleInsert(InsertionContext context, LookupElement item)
                                {
                                    CaretModel caretModel = context.getEditor().getCaretModel();
                                    caretModel.moveToOffset(caretModel.getOffset() - 1);
                                }
                            })
                        );
                    }
                    // ---------------
                }
            }
        );
    }

}
