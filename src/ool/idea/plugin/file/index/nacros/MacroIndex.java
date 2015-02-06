package ool.idea.plugin.file.index.nacros;

import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 2/6/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroIndex
{
    @NonNls
    public static final String MACRO_REGISTRY_NAMESPACE = "macros";
    @NonNls
    public static final String DEFAULT_NAMESPACE = "oxy";
    @NonNls
    public static final String DEBUG_NAMESPACE = "debug";
    @NonNls
    public static final String UTILS_NAMESPACE = "utils";

    public static boolean isInMacroNamespace(@NotNull String fqn)
    {
        return fqn.startsWith(MacroIndex.DEFAULT_NAMESPACE + ".")
                || fqn.startsWith(MacroIndex.DEBUG_NAMESPACE + ".")
                || fqn.startsWith(MacroIndex.UTILS_NAMESPACE + ".");
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

}
