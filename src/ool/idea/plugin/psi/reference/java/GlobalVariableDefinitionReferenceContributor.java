package ool.idea.plugin.psi.reference.java;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.reference.innerjs.globals.GlobalVariableDefinitionReference;
import org.jetbrains.annotations.NotNull;

/**
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableDefinitionReferenceContributor extends PsiReferenceContributor
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
                    PsiElement ref;

                    if( ! (literalExpression.getValue() instanceof String))
                    {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    String text = (String) literalExpression.getValue();

                    if((ref = OxyTemplateIndexUtil.getGlobalVariableRefrence(text, element.getProject())) != null
                            && ref.isEquivalentTo(literalExpression))
                    {
                        return new PsiReference[]{new GlobalVariableDefinitionReference(literalExpression)};
                    }

                    return PsiReference.EMPTY_ARRAY;
                }
            }
        );
    }

}
