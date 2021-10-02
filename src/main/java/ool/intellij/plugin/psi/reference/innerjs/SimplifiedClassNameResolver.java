package ool.intellij.plugin.psi.reference.innerjs;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ool.intellij.plugin.psi.OxyTemplateHelper;

import com.intellij.lang.javascript.psi.JSType;
import com.intellij.lang.javascript.psi.types.JSRecursiveTypeVisitor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.AllClassesSearchExecutor;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class SimplifiedClassNameResolver extends JSRecursiveTypeVisitor
{
    @NonNls
    private static final String HIBERNATE_ENTITY_ANNOTATION_FQN = "javax.persistence.Entity";

    private final Logger logger = Logger.getInstance(getClass());

    private final PsiFile scope;

    private final List<PsiClass> resolvedClassList;

    public SimplifiedClassNameResolver(@NotNull PsiFile scope)
    {
        this.scope = scope;
        this.resolvedClassList = new LinkedList<>();
    }

    @Override
    public void visitJSTypeImpl(JSType type)
    {
        PsiClass resolvedClass;

        if ((resolvedClass = resolveJavaSimplifiedClass(type.getTypeText())) != null
                && resolvedClass.getQualifiedName() != null)
        {
            // js library hack ------------------
            try
            {
                Field field = type.getClass().getDeclaredField("myType");
                field.setAccessible(true);
                field.set(type, InnerJsJavaTypeConverter.simplify(resolvedClass.getQualifiedName()));

                resolvedClassList.add(resolvedClass);
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                logger.error("Javascript api change !", e);
            }
            // ----------------------------------
        }

        super.visitJSTypeImpl(type);
    }

    @NotNull
    public List<PsiClass> getResolvedClassList()
    {
        return resolvedClassList;
    }

    @Nullable
    protected PsiClass resolveJavaSimplifiedClass(@NotNull String simpleTypeName)
    {
        Module module;

        if ( ! Character.isUpperCase(simpleTypeName.charAt(0)) || simpleTypeName.contains(".")
                || (module = ModuleUtilCore.findModuleForPsiElement(scope)) == null)
        {
            return null;
        }

        GlobalSearchScope searchScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
        final List<PsiClass> classes = new LinkedList<>();

        AllClassesSearchExecutor.processClassesByNames(scope.getProject(), searchScope, Collections.singletonList(simpleTypeName),
                psiClass -> {
                    classes.add(psiClass);
                    return true;
                });

        if (classes.size() == 1)
        {
            return classes.get(0);
        }
        else if (classes.size() > 1)
        {
            for (PsiClass aClass : classes)
            {
                if (OxyTemplateHelper.hasAnnotation(aClass, HIBERNATE_ENTITY_ANNOTATION_FQN)
                        || ExtenderIndex.getExtenders(aClass).size() != 0)
                {
                    return aClass;
                }
            }
        }

        return null;
    }

}
