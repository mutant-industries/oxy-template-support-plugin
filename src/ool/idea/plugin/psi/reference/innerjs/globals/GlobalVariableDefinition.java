package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.impl.FakePsiElement;
import ool.idea.plugin.psi.OxyTemplateNamedElement;
import org.jetbrains.annotations.NotNull;

/**
 * Fake element
 *
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableDefinition extends FakePsiElement implements OxyTemplateNamedElement
{
    private PsiLiteralExpression literalExpression;

    public GlobalVariableDefinition(PsiLiteralExpression literalExpression)
    {
        this.literalExpression = literalExpression;
    }

    public PsiLiteralExpression getLiteralExpression()
    {
        return literalExpression;
    }

    @Override
    @NotNull
    public Language getLanguage() {
        return JavascriptLanguage.INSTANCE;
    }

    @Override
    public PsiElement getParent()
    {
        return literalExpression;
    }

    @Override
    public PsiElement setName(@NotNull String name)
    {
        PsiLiteralExpression newExpr = (PsiLiteralExpression) JavaPsiFacade.getInstance(literalExpression.getProject())
                .getElementFactory().createExpressionFromText("\"" + name + "\"", null);

        literalExpression.replace(newExpr);

        literalExpression = newExpr;

        return this;
    }

    @Override
    public String getName()
    {
        return (String) literalExpression.getValue();
    }

    @Override
    public boolean canNavigate() {
        return PsiNavigationSupport.getInstance().canNavigate(literalExpression);
    }

    @Override
    public Project getProject()
    {
        return literalExpression.getProject();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement()
    {
        return literalExpression;
    }

}
