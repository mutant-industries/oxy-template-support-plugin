package ool.idea.plugin.psi.reference.innerjs;

import com.intellij.lang.javascript.index.JSSymbolUtil;
import com.intellij.lang.javascript.nashorn.resolve.NashornJSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSQualifiedName;
import com.intellij.lang.javascript.psi.JSQualifiedNameImpl;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.BaseJSSymbolProcessor;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.lang.javascript.psi.resolve.WalkUpResolveProcessor;
import com.intellij.lang.javascript.psi.types.JSContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import java.util.Iterator;
import java.util.List;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.idea.plugin.psi.reference.MacroReferenceResolver;
import ool.idea.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsReferenceExpressionResolver extends NashornJSReferenceExpressionResolver
{
    public InnerJsReferenceExpressionResolver(JSReferenceExpressionImpl expression, PsiFile file)
    {
        super(expression, file);
    }

    @Override
    public ResolveResult[] doResolve()
    {
        if (myReferencedName == null)
        {
            return ResolveResult.EMPTY_ARRAY;
        }

        if(PsiTreeUtil.getParentOfType(myRef, JSCallExpression.class) != null)
        {
            ResolveResult[] results = ResolveCache.getInstance(myRef.getProject())
                    .resolveWithCaching(myRef, getMacroReferenceResolver(), false, false, myRef.getContainingFile());

            if(results.length > 1 && myRef.getParent() instanceof JSCallExpression)
            {
                ResolveResult result;

                if((result = OxyTemplateHelper.multiResolveWithIncludeSearch(myRef, results)) != null)
                {
                    return new ResolveResult[]{result};
                }

                return results;
            }
            else if(results.length > 0)
            {
                return results;
            }
        }

        ResolveResult[] parentResult = super.doResolve();

        if(parentResult == null || parentResult.length == 0 && isGlobalVariableSuspect(myRef))
        {
            PsiElement reference;
            // global
            if((reference = OxyTemplateIndexUtil.getGlobalVariableRefrence(myReferencedName, myRef.getProject())) != null
                    && reference instanceof PsiLiteralExpression)
            {
                return new JSResolveResult[]{new JSResolveResult(new GlobalVariableDefinition((PsiLiteralExpression)reference))};
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
                expression.getFirstChild() instanceof JSReferenceExpression);
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
        List taggedResolveResults = processor.getTaggedResolveResults();
        if (taggedResolveResults.isEmpty() || ((WalkUpResolveProcessor.TaggedResolveResult) taggedResolveResults.get(0)).hasTag(WalkUpResolveProcessor.ResolveResultTag.PARTIAL))
        {
            Module module;
            if (JSSymbolUtil.isAccurateReferenceExpression(this.myRef))
            {
                if (this.myQualifier instanceof JSReferenceExpression)
                {
                    PsiElement typeInfo = ((JSReferenceExpression) this.myQualifier).resolve();
                    if (typeInfo instanceof PsiClass)
                    {
                        List module1 = this.resolveInPsiClass((PsiClass) typeInfo, true);
                        return module1.isEmpty() ? ResolveResult.EMPTY_ARRAY : (ResolveResult[]) module1.toArray(new ResolveResult[module1.size()]);
                    }
                }

                JavaPsiFacade typeInfo1 = JavaPsiFacade.getInstance(this.myContainingFile.getProject());
                module = ModuleUtilCore.findModuleForPsiElement(this.myContainingFile);
                JSQualifiedName psiFacade = JSSymbolUtil.getAccurateReferenceName(this.myRef);
                if (psiFacade != null)
                {
                    psiFacade = ((JSQualifiedNameImpl) psiFacade).withoutInnermostComponent("Packages");
                }

                if (psiFacade != null)
                {
                    String scope = psiFacade.getQualifiedName();
                    if (module != null)
                    {
                        PsiClass javaResults = typeInfo1.findClass(scope, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));
                        if (javaResults != null)
                        {
                            return new ResolveResult[]{new JSResolveResult(javaResults)};
                        }
                    }

                    PsiPackage javaResults1 = typeInfo1.findPackage(scope);
                    if (javaResults1 != null)
                    {
                        return new ResolveResult[]{new JSResolveResult(javaResults1)};
                    }
                }
            }

            BaseJSSymbolProcessor.TypeInfo typeInfo2 = processor.getTypeInfo();
            if (!typeInfo2.myContextLevels.isEmpty() && (module = ModuleUtilCore.findModuleForPsiElement(this.myContainingFile)) != null)
            {
                JavaPsiFacade psiFacade1 = JavaPsiFacade.getInstance(this.myContainingFile.getProject());
                GlobalSearchScope scope1 = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
                SmartList javaResults2 = new SmartList();
                boolean contextResolvesToJavaClass = false;
                Iterator i$ = typeInfo2.myContextLevels.iterator();

                while (i$.hasNext())
                {
                    BaseJSSymbolProcessor.TypeInfo.ContextLevel level = (BaseJSSymbolProcessor.TypeInfo.ContextLevel) i$.next();
                    if (level.myRelativeLevel == 0)
                    {
                        PsiClass aClass = psiFacade1.findClass(StringUtil.join(level.myQualifiedName, "."), scope1);
                        if (aClass != null)
                        {
                            contextResolvesToJavaClass = true;
                            List results = this.resolveInPsiClass(aClass, level.myStaticOrInstance == JSContext.STATIC);
                            javaResults2.addAll(results);
                        }
                    }
                }

                if (contextResolvesToJavaClass)
                {
                    return javaResults2.isEmpty() ? ResolveResult.EMPTY_ARRAY : (ResolveResult[]) javaResults2.toArray(new ResolveResult[javaResults2.size()]);
                }
            }
        }

        return WalkUpResolveProcessor.convertResults(taggedResolveResults);
    }

    /**
     * modified supertype method, fixes the followin issue: https://youtrack.jetbrains.com/issue/IDEA-138078
     *
     * @param aClass
     * @param isStatic
     * @return
     */
    @NotNull
    private List<ResolveResult> superResolveInPsiClassModified(PsiClass aClass, boolean isStatic)
    {
        SmartList results = new SmartList();
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

                        if (parametersCount == (inDefinitionExpr ? 1 : 0))
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
            boolean isMemberStatic = ((PsiModifierListOwner) var17).hasModifierProperty("static");

            String var20 = isStatic && ! isMemberStatic ? "javascript.instance.member.is.not.accessible.message" :
                    ( ! isStatic && isMemberStatic ? "javascript.static.member.is.not.accessible.message" :
                            (!((PsiModifierListOwner) var17).hasModifierProperty("public") ? "javascript.element.is.not.accessible.message" : null));

            JSResolveResult var19 = var20 == null ?
                    new JSResolveResult((PsiElement) var17) :
                    new JSResolveResult((PsiElement) var17, null, var20);

            results.add(var19);
        }

        return results;
    }
    // --------------------------------------------------------------------------------------------------------------

}
