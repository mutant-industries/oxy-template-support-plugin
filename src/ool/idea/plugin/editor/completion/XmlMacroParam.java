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
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameter;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.JSSourceElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.OrderedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.psi.MacroAttribute;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroNameIdentifier;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
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
                    MacroCall macroCall = PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroCall.class);
                    MacroNameIdentifier macroFunction;
                    MacroName macroName;

                    if(macroCall == null || (macroName = macroCall.getMacroName()) == null
                            || (macroFunction = macroName.getMacroFunction()) == null || macroFunction.getReference() == null)
                    {
                        return;
                    }

                    PsiElement reference = macroFunction.getReference().resolve();

                    if(reference == null || ( ! (reference.getLastChild() instanceof JSFunctionExpression) &&
                        ! (reference instanceof PsiClass)))
                    {
                        return;
                    }

                    List<String> suggestions = reference.getLastChild() instanceof JSFunctionExpression ?
                            getMacroParamNameSuggestions((JSFunctionExpression) reference.getLastChild()) : getMacroParamNameSuggestions((PsiClass)reference);

                    List<MacroAttribute> attributes = macroCall.getMacroAttributeList();

                    main:
                    for(String param : suggestions)
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


    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar)
    {
        return typeChar == ' ' && position.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME_IDENTIFIER;
    }

    @NotNull
    private static List<String> getMacroParamNameSuggestions(@NotNull PsiClass psiClass)
    {
        ArrayList<String> params = new ArrayList<String>();

        PsiMethod[] methods = psiClass.findMethodsByName("execute", false);

        if(methods.length == 0)
        {
            return params;
        }

        PsiMethod executeMethod = methods[0];

        if(executeMethod.getBody() == null)
        {
            return params;
        }

        String methodContent = executeMethod.getBody().getText();

        Matcher matcher;

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

        return params;
    }

    @NotNull
    private static List<String> getMacroParamNameSuggestions(@NotNull JSFunctionExpression reference)
    {
        final OrderedSet<String> params = new OrderedSet<String>();

        JSParameter[] referenceParams = reference.getParameters();
        JSSourceElement[] sourceElements = reference.getBody();

        if(reference.getKind() != JSFunction.FunctionKind.SIMPLE || referenceParams.length < 1 || sourceElements.length == 0)
        {
            return params;
        }

        String firstParamName = referenceParams[0].getName();
        final Pattern paramNamePattern = Pattern.compile("^" + firstParamName + ".(\\w+)");

        sourceElements[0].acceptChildren(new PsiRecursiveElementVisitor()
        {
            @Override
            public void visitElement(PsiElement element)
            {
                if ((element instanceof JSReferenceExpression))
                {
                    String referenceName = element.getText();
                    Matcher matcher = paramNamePattern.matcher(referenceName);

                    if(matcher.find())
                    {
                        params.add(matcher.group(1));
                    }
                }

                super.visitElement(element);
            }
        });

        return params;
    }

}
