package ool.intellij.plugin.file.index.nacro;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ool.template.core.Macro;

import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroIndex
{
    public static final String MACRO_INTERFACE_FQN = Macro.class.getName();
    @NonNls
    public static final String MACRO_CLASS_NAME_SUFFIX = "Macro";
    @NonNls
    public static final List<String> macrosInDebugNamespace = new LinkedList<>();
    @NonNls
    public static final String MACRO_REGISTRY_NAMESPACE = "macros";
    @NonNls
    public static final String DEFAULT_NAMESPACE = "oxy";
    @NonNls
    public static final String DEBUG_NAMESPACE = "debug";
    @NonNls
    public static final String UTILS_NAMESPACE = "utils";
    @NonNls
    public static final String REPEAT_MACRO_VARIABLE_DEFINITION = "varName";
    @NonNls
    public static final String REPEAT_MACRO_INDEX_DEFINITION = "indexName";
    @NonNls
    public static final String REPEAT_MACRO_LIST_DEFINITION = "list";

    public static final List<String> javaMacroNamespaces = Arrays.asList(DEFAULT_NAMESPACE, DEBUG_NAMESPACE);

    public static boolean isInMacroNamespace(@NotNull String fqn)
    {
        String normalizedFqn = normalizeMacroName(fqn);

        return normalizedFqn.startsWith(MacroIndex.DEFAULT_NAMESPACE + ".")
                || normalizedFqn.startsWith(MacroIndex.DEBUG_NAMESPACE + ".")
                || normalizedFqn.startsWith(MacroIndex.UTILS_NAMESPACE + ".");
    }

    public static boolean isMacroRootNamespace(@NotNull String fqn)
    {
        return fqn.equals(DEFAULT_NAMESPACE) || fqn.equals(DEBUG_NAMESPACE) || fqn.equals(UTILS_NAMESPACE);
    }

    public static boolean isMacroRootNamespace(@NotNull String fqn, boolean includeMacroRegistryNamespace)
    {
        return includeMacroRegistryNamespace && (fqn.equals(MACRO_REGISTRY_NAMESPACE)
                || fqn.equals(MACRO_REGISTRY_NAMESPACE + "." + DEFAULT_NAMESPACE)) || isMacroRootNamespace(fqn);
    }

    public static boolean isInMacroDefinition(@NotNull JSReferenceExpression expression)
    {
        String referenceText = expression.getText();
        JSReferenceExpression topReference = ! (expression.getParent() instanceof JSReferenceExpression) ? expression
                : PsiTreeUtil.getTopmostParentOfType(expression, JSReferenceExpression.class);

        return topReference != null && topReference.getParent() instanceof JSDefinitionExpression
                && ! MacroIndex.isMacroRootNamespace(referenceText, true);
    }

    @NotNull
    public static String normalizeMacroName(@NotNull String expression)
    {
        return expression.replaceAll("\\s*\\.\\s*", ".");
    }

    public static boolean checkJavaMacroNamespace(@NotNull String macroName)
    {
        return macroName.contains(".") && javaMacroNamespaces.contains(macroName.substring(0, macroName.indexOf(".")));

    }

    @Nullable
    public static PsiClass getJavaMacroInterface(@NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        return JavaPsiFacade.getInstance(project).findClass(MACRO_INTERFACE_FQN, allScope);
    }

}
