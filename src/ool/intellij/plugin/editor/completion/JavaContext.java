package ool.intellij.plugin.editor.completion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ool.intellij.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;
import ool.intellij.plugin.psi.reference.innerjs.ExtenderProvider;
import ool.common.web.model.BaseExtender;
import ool.common.web.model.Extender;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.resolve.JSSimpleTypeProcessor;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.types.JSCompositeTypeImpl;
import com.intellij.lang.javascript.psi.types.JSTypeImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.TokenType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.InheritanceUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 4/29/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaContext extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*");

    private final String EXTENDER_INTERFACE_FQN = Extender.class.getName();

    private final String EXTENDER_BASE_CLASS_FQN = BaseExtender.class.getName();

    private final List<String> shippedBaseExtenderMethods = Arrays.asList("getObject", "getValue", "isVisibleProperty",
            "containsProperty", "getPropertyKeys", "toString");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        Module module;
        PsiElement element = parameters.getOriginalFile().getViewProvider().findElementAt(parameters.getOffset() - 1,
                OxyTemplateInnerJs.INSTANCE);

        if (element == null || element.getNode().getElementType() != JSTokenTypes.DOT &&
                (element = element.getPrevSibling()) == null)
        {
            return;
        }
        if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
        {
            element = element.getPrevSibling();
        }
        while (element != null && element.getNode().getElementType() != JSTokenTypes.DOT)
        {
            element = element.getLastChild();

            if (element instanceof PsiErrorElement)
            {
                element = element.getPrevSibling();
            }
        }
        if (element == null || element.getNode().getElementType() != JSTokenTypes.DOT
                || (element = element.getPrevSibling()) == null)
        {
            return;
        }
        if (element.getNode().getElementType() == TokenType.WHITE_SPACE)
        {
            element = element.getPrevSibling();
        }
        if ( ! (element instanceof JSExpression)
                || (module = ModuleUtilCore.findModuleForPsiElement(parameters.getOriginalFile())) == null)
        {
            return;
        }

        JSSimpleTypeProcessor typeProcessor = new JSSimpleTypeProcessor();
        JSTypeEvaluator.evaluateTypes((JSExpression) element, parameters.getOriginalFile()
                .getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE), typeProcessor);
        JSType type = typeProcessor.getType();
        List<String> possibleJavaTypes = new LinkedList<>();

        if (type != null)
        {
            if (type instanceof JSCompositeTypeImpl)
            {
                possibleJavaTypes.addAll(((JSCompositeTypeImpl) type).getTypes().stream()
                        .filter(jsType -> jsType instanceof JSTypeImpl)
                        .map(JSType::getTypeText)
                        .collect(Collectors.toList()));
            }
            else if (type instanceof JSTypeImpl)
            {
                possibleJavaTypes.add(type.getTypeText());
            }
        }

        JavaPsiFacade facade = JavaPsiFacade.getInstance(parameters.getOriginalFile().getProject());
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
        List<PsiClass> possibleInstancesOf = new LinkedList<>();
        boolean suggestionsFound = false;
        PsiClass aClass;

        for (String possibleType : possibleJavaTypes)
        {
            if ((aClass = facade.findClass(possibleType, scope)) != null)
            {
                possibleInstancesOf.add(aClass);
                possibleInstancesOf.addAll(ExtenderProvider.getExtenders(aClass));
            }
        }

        for (PsiClass psiClass : possibleInstancesOf)
        {
            List<String> alreadySuggested = new LinkedList<>();

            for (PsiMethod method : psiClass.getAllMethods())
            {
                if (method.getContainingClass() == null || Object.class.getName().equals(method.getContainingClass().getQualifiedName())
                        || method.getReturnType() == null || shippedBaseExtenderMethods.contains(method.getName())
                        || ! method.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)
                        || InheritanceUtil.isInheritor(method.getContainingClass(), EXTENDER_INTERFACE_FQN)
                            && ! InheritanceUtil.isInheritor(method.getContainingClass(), EXTENDER_BASE_CLASS_FQN))
                {
                    continue;
                }

                String insertText = method.getName();
                String presentableText = method.getName();

                if (insertText.matches("((^is)|(^get)|(^set))[A-Z].*"))
                {
                    insertText = presentableText = StringUtil.decapitalize(insertText.replaceFirst("(^is)|(^get)|(^set)", ""));
                }
                else
                {
                    presentableText = method.getPresentation() == null ? presentableText + "()" :
                            method.getPresentation().getPresentableText();
                    insertText += "()";
                }

                if (alreadySuggested.contains(insertText))
                {
                    continue;
                }

                result.consume(LookupElementBuilder.create(method, insertText)
                        .withIcon(method.getIcon(0))
                        .withTypeText(method.getReturnType().getPresentableText(), true)
                        .withTailText(" (" + psiClass.getContainingFile().getName() + ")", true)
                        .withPresentableText(presentableText)
                        .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                        .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));

                alreadySuggested.add(insertText);

                suggestionsFound = true;
            }
        }

        if (suggestionsFound)
        {
            result.stopHere();
        }
    }

}
