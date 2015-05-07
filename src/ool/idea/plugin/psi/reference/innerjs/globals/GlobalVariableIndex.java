package ool.idea.plugin.psi.reference.innerjs.globals;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class GlobalVariableIndex implements CachedValueProvider<Map<String, GlobalVariableDefinition>>
{
    private static final Key<CachedValue<Map<String, GlobalVariableDefinition>>> GLOBAL_VARIABLES_KEY =
            Key.create("GLOBAL_VARIABLES_KEY");

    @NonNls
    private static final String MODEL_PROVIDER_REGISTRY_FQN = "ool.web.model.ondemand.ModelProviderRegistry";
    @NonNls
    private static final String GLOBAL_MODEL_PROVIDER_FQN = "ool.web.model.ondemand.GlobalModelProvider";
    @NonNls
    private static final String REGISTER_METHOD_NAME = "register";
    @NonNls
    private static final String GET_NAME_METHOD_NAME = "getName";
    @NonNls
    private static final String REGISTRAR_BEANS_FILE_NAME = "web.xml";

    private final Project project;

    private GlobalVariableIndex(Project project)
    {
        this.project = project;
    }

    @NotNull
    public static Map<String, GlobalVariableDefinition> getGlobals(@NotNull Project project)
    {
        // just don't throw any cache related exceptions in projects, where no globals are defined
        if (FilenameIndex.getFilesByName(project, REGISTRAR_BEANS_FILE_NAME,
                ProjectScope.getProjectScope(project), false).length == 0)
        {
            return Collections.emptyMap();
        }

        CachedValue<Map<String, GlobalVariableDefinition>> cached = project.getUserData(GLOBAL_VARIABLES_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(project).createCachedValue(new GlobalVariableIndex(project), false);

            project.putUserData(GLOBAL_VARIABLES_KEY, cached);
        }

        return cached.getValue();
    }

    @Nullable
    @Override
    public Result<Map<String, GlobalVariableDefinition>> compute()
    {
        final GlobalSearchScope scope = ProjectScope.getAllScope(project);
        Map<String, GlobalVariableDefinition> result = new HashMap<>();
        List<PsiElement> cacheDependencies = new LinkedList<>();
        PsiClass registryClass;
        PsiMethod[] methods;
        PsiElement element;

        Collections.addAll(cacheDependencies, FilenameIndex.getFilesByName(project, REGISTRAR_BEANS_FILE_NAME,
                ProjectScope.getProjectScope(project), false));

        if ((registryClass = JavaPsiFacade.getInstance(project).findClass(MODEL_PROVIDER_REGISTRY_FQN, scope)) != null
                && (methods = registryClass.findMethodsByName(REGISTER_METHOD_NAME, false)).length > 0)
        {
            cacheDependencies.add(registryClass);

            PsiMethod registerMethod = methods[0];

            for (PsiReference reference : MethodReferencesSearch.search(registerMethod, scope, true).findAll())
            {
                element = reference.getElement();

                if (element == null || ! (element.getParent() instanceof PsiMethodCallExpression)
                        || (element = ((PsiMethodCallExpression) element.getParent()).getArgumentList()) == null
                        || ((PsiExpressionList) element).getExpressions().length != 2)
                {
                    continue;
                }

                PsiExpression expression = ((PsiExpressionList) element).getExpressions()[0];
                Object value = JavaConstantExpressionEvaluator.computeConstantExpression(expression, false);

                if ( ! (value instanceof String) || StringUtils.isEmpty((String) value))
                {
                    continue;
                }

                result.put((String) value, new GlobalVariableDefinition(expression, (String) value));
                cacheDependencies.add(expression);
            }
        }

        if ((registryClass = JavaPsiFacade.getInstance(project).findClass(GLOBAL_MODEL_PROVIDER_FQN, scope)) != null
                && (methods = registryClass.findMethodsByName(GET_NAME_METHOD_NAME, false)).length > 0)
        {
            cacheDependencies.add(registryClass);

            PsiMethod getNameMethod = methods[0];

            for (PsiMethod method : OverridingMethodsSearch.search(getNameMethod).findAll())
            {
                PsiReturnStatement returnStatement;
                PsiExpression expression;

                if ((returnStatement = PsiTreeUtil.findChildOfType(method, PsiReturnStatement.class)) == null
                        || (expression = PsiTreeUtil.findChildOfType(returnStatement, PsiExpression.class)) == null)
                {
                    continue;
                }

                Object value = JavaConstantExpressionEvaluator.computeConstantExpression(expression, false);

                if ( ! (value instanceof String) || StringUtils.isEmpty((String) value))
                {
                    continue;
                }

                result.put((String) value, new GlobalVariableDefinition(expression, (String) value));
                cacheDependencies.add(expression);
            }
        }

        return Result.create(result, cacheDependencies);
    }

}
