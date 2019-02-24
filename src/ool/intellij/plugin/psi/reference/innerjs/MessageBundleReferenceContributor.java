package ool.intellij.plugin.psi.reference.innerjs;

import com.intellij.lang.javascript.psi.JSArgumentList;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * 2/23/19
 *
 * @author Mutant Industries ltd. <mutant-industries@gmx.com>
 */
public class MessageBundleReferenceContributor extends PsiReferenceContributor
{
    private static final String MESSAGE_GET_FUNCTION_NAME = "getMessage";
    private static final String MESSAGE_BUNDLE_NAME = "ool.localization.messages";

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar)
    {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(JSLiteralExpression.class),
            new PsiReferenceProvider()
            {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context)
                {
                    JSLiteralExpression literalExpression = (JSLiteralExpression) element;
                    PsiElement argumentList;
                    JSExpression[] arguments;
                    JSCallExpression callExpression;
                    JSExpression methodExpression;
                    PsiElement reference;

                    if ( ! (literalExpression.getValue() instanceof String))
                    {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    if ( ! ((argumentList = element.getParent()) instanceof JSArgumentList)
                            || ! (argumentList.getParent() instanceof JSCallExpression))
                    {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    arguments = ((JSArgumentList) argumentList).getArguments();

                    if ( ! element.isEquivalentTo(arguments[0])) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    callExpression = (JSCallExpression) argumentList.getParent();
                    methodExpression = callExpression.getMethodExpression();

                    if ( ! (methodExpression instanceof JSReferenceExpression) || (reference = ((JSReferenceExpression) methodExpression).resolve()) == null
                            || ! (reference instanceof JSDefinitionExpression) || ! reference.textMatches(MESSAGE_GET_FUNCTION_NAME))
                    {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    return new PropertyReference[]{new PropertyReference((String) literalExpression.getValue(),
                            element, MESSAGE_BUNDLE_NAME, false)};
                }
            }
        );
    }

}
