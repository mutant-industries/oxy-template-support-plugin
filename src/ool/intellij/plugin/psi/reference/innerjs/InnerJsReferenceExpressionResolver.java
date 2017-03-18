package ool.intellij.plugin.psi.reference.innerjs;

import java.util.Iterator;
import java.util.List;

import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.intellij.plugin.psi.reference.MacroReferenceResolver;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;
import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableIndex;

import com.intellij.lang.javascript.index.JSSymbolUtil;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSQualifiedName;
import com.intellij.lang.javascript.psi.JSQualifiedNameImpl;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.resolve.JSContextLevel;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.lang.javascript.psi.resolve.JSTaggedResolveResult;
import com.intellij.lang.javascript.psi.resolve.WalkUpResolveProcessor;
import com.intellij.lang.javascript.psi.types.JSContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsReferenceExpressionResolver extends NashornJSReferenceExpressionResolver
{
    public InnerJsReferenceExpressionResolver(JSReferenceExpressionImpl expression, boolean ignorePerformanceLimits)
    {
        super(expression, ignorePerformanceLimits);
    }

    @Override
    public ResolveResult[] resolve(@NotNull JSReferenceExpressionImpl expression, boolean incompleteCode)
    {
        if (myReferencedName == null)
        {
            return ResolveResult.EMPTY_ARRAY;
        }

        if (PsiTreeUtil.getParentOfType(myRef, JSCallExpression.class) != null)
        {
            ResolveResult[] results = ResolveCache.getInstance(myRef.getProject())
                    .resolveWithCaching(myRef, getMacroReferenceResolver(), false, false, myRef.getContainingFile());

            if (results.length > 1 && myRef.getParent() instanceof JSCallExpression)
            {
                ResolveResult result;

                if ((result = OxyTemplateHelper.multiResolveWithIncludeSearch(myRef, results)) != null)
                {
                    return new ResolveResult[]{result};
                }

                return results;
            }
            else if (results.length > 0)
            {
                return results;
            }
        }

        ResolveResult[] parentResult = super.resolve(expression, incompleteCode);

        if (parentResult.length == 0 && isGlobalVariableSuspect(myRef))
        {
            GlobalVariableDefinition reference;
            // global
            if ((reference = GlobalVariableIndex.getGlobals(myRef.getProject()).get(myReferencedName)) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        return parentResult;
    }

//    @Override
    protected List<ResolveResult> resolveInPsiClass(PsiClass aClass, boolean isStatic)
    {
        List<ResolveResult> result = superResolveInPsiClassModified(aClass, isStatic);

        for (PsiClass extender : ExtenderProvider.getExtenders(aClass))
        {
            if (result.size() > 0)
            {
                break;
            }

            result.addAll(superResolveInPsiClassModified(extender, isStatic));
        }

        return result;
    }

    @NotNull
    protected MacroReferenceResolver getMacroReferenceResolver()
    {
        return MacroReferenceResolver.DEFAULT;
    }

    // --------------------------------------------------------------------------------------------------------------

    public static boolean isGlobalVariableSuspect(@NotNull PsiElement element)
    {
        JSReferenceExpression expression = element instanceof JSReferenceExpression ? (JSReferenceExpression) element
                : PsiTreeUtil.getParentOfType(element, JSReferenceExpression.class);

        return ! (expression == null || expression.getParent() instanceof JSProperty ||
                expression.getFirstChild() instanceof JSExpression);
    }

    /**
     *  !!    TEMP CODE -  https://youtrack.jetbrains.com/issue/IDEA-138078    !!
     *
     * duplicated code to {@link NashornJSReferenceExpressionResolver#getResultsFromProcessor(WalkUpResolveProcessor)}
     *  - {@link super#resolveInPsiClass(PsiClass, boolean)} will one day (hopefully) be protected
     */
    // --------------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------------
    protected ResolveResult[] getResultsFromProcessor(WalkUpResolveProcessor processor)
    {
        List<JSTaggedResolveResult> taggedResolveResults = processor.getTaggedResolveResults();
        if (taggedResolveResults.isEmpty() || (taggedResolveResults.get(0)).hasTag(JSTaggedResolveResult.ResolveResultTag.PARTIAL))
        {
            Module module;
            if (JSSymbolUtil.isAccurateReferenceExpression(this.myRef))
            {
                if (this.myQualifier instanceof JSReferenceExpression)
                {
                    PsiElement qualifierResolve = ((JSReferenceExpression) this.myQualifier).resolve();
                    if (qualifierResolve instanceof PsiClass)
                    {
                        List<ResolveResult> results = this.resolveInPsiClass((PsiClass) qualifierResolve, true);
                        return results.isEmpty() ? ResolveResult.EMPTY_ARRAY : results.toArray(new ResolveResult[results.size()]);
                    }
                }

                JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(this.myContainingFile.getProject());
                module = ModuleUtilCore.findModuleForPsiElement(this.myContainingFile);
                JSQualifiedName qualifiedName = JSSymbolUtil.getAccurateReferenceName(this.myRef);
                if (qualifiedName != null)
                {
                    qualifiedName = ((JSQualifiedNameImpl) qualifiedName).withoutInnermostComponent("Packages");
                }

                if (qualifiedName != null)
                {
                    String qName = qualifiedName.getQualifiedName();
                    if (module != null)
                    {
                        PsiClass aClass = psiFacade.findClass(qName, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));
                        if (aClass != null)
                        {
                            return new ResolveResult[]{new JSResolveResult(aClass)};
                        }
                    }

                    PsiPackage aPackage = psiFacade.findPackage(qName);
                    if (aPackage != null)
                    {
                        return new ResolveResult[]{new JSResolveResult(aPackage)};
                    }
                }
            }

            BaseJSSymbolProcessor.TypeInfo typeInfo = processor.getTypeInfo();
            if (!typeInfo.myContextLevels.isEmpty() && (module = ModuleUtilCore.findModuleForPsiElement(this.myContainingFile)) != null)
            {
                JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(this.myContainingFile.getProject());
                GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
                List<ResolveResult> javaResults = new SmartList();
                boolean contextResolvesToJavaClass = false;
                Iterator var9 = typeInfo.myContextLevels.iterator();

                while (var9.hasNext())
                {
                    JSContextLevel level = (JSContextLevel) var9.next();
                    JSQualifiedName qualifiedName = level.myNamespace.getQualifiedName();
                    if (level.myRelativeLevel == 0 && qualifiedName != null)
                    {
                        PsiClass aClass = psiFacade.findClass(qualifiedName.getQualifiedName(), scope);
                        if (aClass != null)
                        {
                            contextResolvesToJavaClass = true;
                            List<ResolveResult> results = this.resolveInPsiClass(aClass, level.myNamespace.getJSContext() == JSContext.STATIC);
                            javaResults.addAll(results);
                        }
                    }
                }

                if (contextResolvesToJavaClass)
                {
                    return javaResults.toArray(ResolveResult.EMPTY_ARRAY);
                }
            }
        }

        return processor.getResults();
    }

    /**
     * modified supertype method, fixes the following issue: https://youtrack.jetbrains.com/issue/IDEA-138078
     *
     * @param aClass
     * @param isStatic
     * @return
     */
    @NotNull
    private List<ResolveResult> superResolveInPsiClassModified(PsiClass aClass, boolean isStatic)
    {
        SmartList<ResolveResult> results = new SmartList<>();
        Object candidates;
        Object field;

        if (myParent instanceof JSCallExpression)
        {
            candidates = aClass.findMethodsByName(myReferencedName, true);
        }
        else
        {
            field = aClass.findInnerClassByName(myReferencedName, true);

            if (field == null)
            {
                boolean inDefinitionExpr = myParent instanceof JSDefinitionExpression;

                String prefix = inDefinitionExpr ? "set" : "get";
                String candidate = prefix + StringUtil.capitalize(myReferencedName);
                PsiMethod[] hasStaticModifier = aClass.findMethodsByName(candidate, true);
                PsiMethod[] resolveProblem = hasStaticModifier;

                int resolveResult = hasStaticModifier.length;

                for (int i$1 = 0; i$1 < resolveResult; ++i$1)
                {
                    PsiMethod accessor = resolveProblem[i$1];
                    int parametersCount = accessor.getParameterList().getParametersCount();

                    if (parametersCount == (inDefinitionExpr ? 1 : 0))
                    {
                        field = accessor;
                        break;
                    }
                }

                if(field == null && ! inDefinitionExpr)
                {
                    prefix = "is";
                    candidate = prefix + StringUtil.capitalize(myReferencedName);

                    hasStaticModifier = aClass.findMethodsByName(candidate, true);
                    resolveProblem = hasStaticModifier;

                    resolveResult = hasStaticModifier.length;

                    for (int i$1 = 0; i$1 < resolveResult; ++i$1)
                    {
                        PsiMethod accessor = resolveProblem[i$1];
                        int parametersCount = accessor.getParameterList().getParametersCount();

                        if (parametersCount == 0)
                        {
                            field = accessor;
                            break;
                        }
                    }
                }
            }

            if (field == null)
            {
                field = aClass.findFieldByName(myReferencedName, true);
            }

            candidates = field != null ? new PsiModifierListOwner[]{(PsiModifierListOwner) field} : PsiMember.EMPTY_ARRAY;
        }

        int candidatesLength = ((Object[]) candidates).length;

        for (int var16 = 0; var16 < candidatesLength; ++var16)
        {
            Object var17 = ((Object[]) candidates)[var16];
            boolean var18 = ((PsiModifierListOwner) var17).hasModifierProperty("static");

            String var19 = isStatic && !var18 ? "javascript.instance.member.is.not.accessible.message" :
                    ( ! ((PsiModifierListOwner) var17).hasModifierProperty("public") ? "javascript.element.is.not.accessible.message" : null);

            JSResolveResult var20 = var19 == null ? new JSResolveResult((PsiElement) var17) : new JSResolveResult((PsiElement) var17, null, var19);
            results.add(var20);
        }

        return results;
    }
    // --------------------------------------------------------------------------------------------------------------

}
