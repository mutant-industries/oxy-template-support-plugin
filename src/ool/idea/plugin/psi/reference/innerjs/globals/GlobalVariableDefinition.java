package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValuesManager;
import ool.idea.plugin.psi.OxyTemplateNamedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fake element
 *
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableDefinition extends FakePsiElement implements OxyTemplateNamedPsiElement
{
    private static final Key<CachedValue<JSType>> GLOBAL_VARIABLE_TYPE_KEY = Key.create("GLOBAL_VARIABLE_TYPE_KEY");

    private PsiLiteralExpression literalExpression;

    public GlobalVariableDefinition(@NotNull PsiLiteralExpression literalExpression)
    {
        this.literalExpression = literalExpression;
    }

    public PsiLiteralExpression getLiteralExpression()
    {
        return literalExpression;
    }

    @Override
    @NotNull
    public Language getLanguage()
    {
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

    @Nullable
    public JSType getType()
    {
        CachedValue<JSType> cached = literalExpression.getUserData(GLOBAL_VARIABLE_TYPE_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(literalExpression.getProject())
                    .createCachedValue(new GlobalVariableTypeProvider(literalExpression), false);

            literalExpression.putUserData(GLOBAL_VARIABLE_TYPE_KEY, cached);
        }

        return cached.getValue();
    }

    @Override
    public String getName()
    {
        return (String) literalExpression.getValue();
    }

    @Override
    public boolean canNavigate()
    {
        return PsiNavigationSupport.getInstance().canNavigate(literalExpression);
    }

    @NotNull
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
