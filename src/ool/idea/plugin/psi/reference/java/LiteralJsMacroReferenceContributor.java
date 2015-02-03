package ool.idea.plugin.psi.reference.java;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.psi.reference.MacroReferenceSet;
import org.jetbrains.annotations.NotNull;

/**
 * 1/20/15
 * TODO refactor - split, override accept()
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class LiteralJsMacroReferenceContributor extends PsiReferenceContributor
{
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar)
    {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
            new PsiReferenceProvider()
            {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context)
                {
                    PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                    PsiMethodCallExpression callExpression;

                    if( ! (literalExpression.getValue() instanceof String))
                    {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    if((callExpression = PsiTreeUtil
                            .getParentOfType(literalExpression, PsiMethodCallExpression.class)) != null)
                    {
                        PsiExpression[] parameters = callExpression.getArgumentList().getExpressions();
                        PsiReferenceExpression expression = callExpression.getMethodExpression();
                        String callText = expression.getText();

                        if(callText.contains("ageUpdater.update"))  // TODO type check
                        {
                            if(parameters.length > 0 && literalExpression.isEquivalentTo(parameters[0]))
                            {
                                // TODO path completion / reference
                            }
                            else if(parameters.length > 1 && literalExpression.isEquivalentTo(parameters[1]))
                            {
                                return new MacroReferenceSet(literalExpression).getAllReferences();
                            }
                        }
                    }

                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );
    }

}
