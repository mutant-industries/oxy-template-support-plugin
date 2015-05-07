package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.psi.JSType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
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

    private PsiExpression expression;

    private String name;

    public GlobalVariableDefinition(@NotNull PsiExpression expression, @NotNull String name)
    {
        this.expression = expression;
        this.name = name;
    }

    public PsiExpression getExpression()
    {
        return expression;
    }

    @Override
    @NotNull
    public Language getLanguage()
    {
        return OxyTemplateInnerJs.INSTANCE;
    }

    @Override
    public PsiElement getParent()
    {
        return expression;
    }

    @Override
    public PsiElement setName(@NotNull String name)
    {
        PsiLiteralExpression newExpr = (PsiLiteralExpression) JavaPsiFacade.getInstance(expression.getProject())
                .getElementFactory().createExpressionFromText("\"" + name + "\"", null);

        if (expression instanceof PsiLiteralExpression)
        {
            expression.replace(newExpr);
            expression = newExpr;
        }
        else if (expression instanceof PsiReferenceExpression)
        {
            PsiElement resolve;
            PsiLiteralExpression literalExpression;

            if ((resolve = ((PsiReferenceExpression) expression).resolve()) instanceof PsiField
                    && (literalExpression = PsiTreeUtil.getChildOfType(resolve, PsiLiteralExpression.class)) != null)
            {
                literalExpression.replace(newExpr);
            }
            else
            {
                throw new UnsupportedOperationException(expression.getClass().getName() + " rename not suported !");
            }
        }
        else
        {
            throw new UnsupportedOperationException(expression.getClass().getName() + " rename not suported !");
        }

        this.name = name;

        return this;
    }

    @Nullable
    public JSType getType()
    {
        CachedValue<JSType> cached = expression.getUserData(GLOBAL_VARIABLE_TYPE_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(expression.getProject())
                    .createCachedValue(new GlobalVariableTypeProvider(expression, name), false);

            expression.putUserData(GLOBAL_VARIABLE_TYPE_KEY, cached);
        }

        return cached.getValue();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean canNavigate()
    {
        return PsiNavigationSupport.getInstance().canNavigate(expression);
    }

    @NotNull
    @Override
    public Project getProject()
    {
        return expression.getProject();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement()
    {
        return expression;
    }

}
