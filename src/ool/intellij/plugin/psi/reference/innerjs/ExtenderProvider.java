package ool.intellij.plugin.psi.reference.innerjs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.SmartHashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 4/28/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class ExtenderProvider
{
    private static final String EXTENDER_INTERFACE_FQN = ool.web.model.ExtenderProvider.class.getName();
    @NonNls
    private static final String CLASS_GETTER_METHOD_MANE = "getExtendedClass";
    @NonNls
    private static final String EXTENDER_GETTER_METHOD_MANE = "provide";
    @NonNls
    private static final String EXTENDERS_BEANS_FILE_NAME = "web.xml";

    private static final Key<CachedValue<HashMultimap<String, PsiClass>>> EXTENDER_PROVIDERS_CACHE_KEY = Key.create("EXTENDER_PROVIDERS_CACHE_KEY");

    @NotNull
    public static Set<PsiClass> getExtenders(@NotNull PsiClass original)
    {
        Set<PsiClass> result = new SmartHashSet<>();
        PsiClass extender;

        for (PsiClass psiClass : getExtenderProvidersFor(original))
        {
            if ((extender = getExtenderFromProvider(psiClass)) != null)
            {
                result.add(extender);
            }
        }

        return result;
    }

    @NotNull
    private static Set<PsiClass> getExtenderProvidersFor(@NotNull final PsiClass original)
    {
        final Project project = original.getProject();

        CachedValue<HashMultimap<String, PsiClass>> cached = project.getUserData(EXTENDER_PROVIDERS_CACHE_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(project).createCachedValue(() -> {
                HashMultimap<String, PsiClass> result = HashMultimap.create();
                List<PsiElement> cacheDependencies = new LinkedList<>();

                PsiClass extenderInterface;

                Collections.addAll(cacheDependencies, FilenameIndex.getFilesByName(project, EXTENDERS_BEANS_FILE_NAME,
                        ProjectScope.getProjectScope(project)));

                if ((extenderInterface = getExtenderInterface(project)) == null)
                {
                    return CachedValueProvider.Result.create(result, cacheDependencies);
                }

                PsiMethod[] methodsByName = extenderInterface.findMethodsByName(CLASS_GETTER_METHOD_MANE, false);

                if (methodsByName.length == 0)
                {
                    return CachedValueProvider.Result.create(result, cacheDependencies);
                }

                PsiType returnType;
                PsiTypeElement typeElement;

                for (PsiMethod method : OverridingMethodsSearch.search(methodsByName[0]).findAll())
                {
                    if ((typeElement = PsiTreeUtil.findChildOfType(method.getReturnTypeElement(), PsiTypeElement.class)) == null
                            || (returnType = typeElement.getType()) == null || returnType.getCanonicalText().equals(returnType.getPresentableText()))
                    {
                        continue;
                    }

                    cacheDependencies.add(method);
                    result.put(returnType.getCanonicalText(), method.getContainingClass());
                }

                return CachedValueProvider.Result.create(result, cacheDependencies);
            }, false);

            project.putUserData(EXTENDER_PROVIDERS_CACHE_KEY, cached);
        }

        return cached.getValue().get(original.getQualifiedName());
    }

    @Nullable
    private static PsiClass getExtenderFromProvider(@NotNull PsiClass extenderProvider)
    {
        PsiMethod[] provideMethods = extenderProvider.findMethodsByName(EXTENDER_GETTER_METHOD_MANE, true);

        if (provideMethods.length < 2)
        {
            return null;
        }

        /**
         * return is always followed by a new expression, which contains class reference or anonymous class
         */
        PsiNewExpression newExpression = PsiTreeUtil.findChildOfType(provideMethods[0], PsiNewExpression.class);

        if (newExpression == null)
        {
            return null;
        }

        PsiJavaCodeReferenceElement extenderClassReference = PsiTreeUtil.getNextSiblingOfType(newExpression.getFirstChild(),
                PsiJavaCodeReferenceElement.class);
        PsiElement result;

        if (extenderClassReference != null && (result = extenderClassReference.resolve()) instanceof PsiClass)
        {
            return (PsiClass) result;
        }

        PsiAnonymousClass anonymousClass = PsiTreeUtil.getNextSiblingOfType(newExpression.getFirstChild(),
                PsiAnonymousClass.class);

        if (anonymousClass != null)
        {
            return anonymousClass;
        }

        return null;
    }

    @Nullable
    private static PsiClass getExtenderInterface(@NotNull Project project)
    {
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

        return JavaPsiFacade.getInstance(project).findClass(EXTENDER_INTERFACE_FQN, allScope);
    }

}
