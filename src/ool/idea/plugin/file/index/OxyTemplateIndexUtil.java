package ool.idea.plugin.file.index;

import com.google.common.collect.HashMultimap;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.collector.JsMacroCollector;
import ool.idea.plugin.file.index.collector.MacroCollector;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameIndexedElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateIndexUtil
{
    private static final Key<CachedValue<String>> MACRO_QUALIFIED_NAME_KEY = Key.create("MACRO_QUALIFIED_NAME_KEY");

    @NotNull
    public static List<PsiElement> getMacroNameReferences(@NotNull String macroName, @NotNull Project project)
    {
        List<PsiElement> references = new ArrayList<PsiElement>();
        PsiClass psiClass;

        references.addAll(getJsMacroNameReferences(macroName, project));

        if(references.size() == 0 && (psiClass = getJavaMacroNameReference(macroName, project)) != null)
        {
            references.add(psiClass);
        }

        return references;
    }

    @Nullable
    public static PsiClass getJavaMacroNameReference(@NotNull String macroName, @NotNull Project project)
    {
        if( ! MacroIndex.checkJavaMacroNamespace(macroName))
        {
            return null;
        }

        final String namespace = macroName.substring(0, macroName.indexOf("."));
        final String javaMacroName = StringUtils.capitalize(macroName.substring(macroName.indexOf(".") + 1)) +
                MacroIndex.JAVA_MACRO_SUFFIX;

        if(StringUtils.isEmpty(javaMacroName) || MacroIndex.macrosInDebugNamespace.contains(javaMacroName)
                && ! MacroIndex.DEBUG_NAMESPACE.equals(namespace))
        {
            return null;
        }

        PsiClass macroInterface;

        if((macroInterface = MacroIndex.getJavaMacroInterface(project)) == null)
        {
            return null;
        }

        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        PsiClass macroClass = ClassInheritorsSearch.INSTANCE.createUniqueResultsQuery(new ClassInheritorsSearch.SearchParameters(macroInterface,
                allScope, true, true, false, new Condition<String>()
        {
            @Override
            public boolean value(String name)
            {
                return javaMacroName.equals(name);
            }
        })).findFirst();

        return macroClass == null || macroClass.isInterface() || macroClass.getModifierList() == null
                || macroClass.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT) ? null : macroClass;
    }

    @NotNull
    public static List<JSElement> getJsMacroNameReferences(@NotNull String macroName, @NotNull final Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        final List<JSElement> results = new ArrayList<JSElement>();

        FileBasedIndex index = FileBasedIndex.getInstance();

        for (VirtualFile file : index.getContainingFiles(JsMacroNameIndex.INDEX_ID, macroName, allScope))
        {
            results.addAll(getJsMacroNameReferences(macroName, file, project));
        }

        return results;
    }

    @NotNull
    public static List<JSElement> getJsMacroNameReferences(@NotNull String macroName, @NotNull VirtualFile file, @NotNull final Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();
        MacroCollector<JSElement, JsMacroNameIndexedElement> processor = new JsMacroCollector(project);

        index.processValues(JsMacroNameIndex.INDEX_ID, macroName, file, processor, allScope);

        return processor.getResult();   // size <= 1
    }

    @NotNull
    public static HashMultimap<String, PsiElement> getMacros(@NotNull Project project)
    {
        return getMacros(project, null);
    }

//    @NotNull
//    public static Map<String, JSElement> getJsMacros(@NotNull Project project)
//    {
//        return getJsMacros(project, null);
//    }

    @NotNull
    public static HashMultimap<String, PsiElement> getMacros(@NotNull Project project, @Nullable Collection<VirtualFile> restrictFiles)
    {
        HashMultimap<String, PsiElement> result = HashMultimap.create();

        for(Map.Entry<String, PsiClass> entry : getJavaMacros(project).entrySet())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        result.putAll(getJsMacros(project, restrictFiles));

        return result;
    }

    @NotNull
    public static Map<String, PsiClass> getJavaMacros(@NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        Map<String, PsiClass> result = new HashMap<String, PsiClass>();

        PsiClass macroInterface;

        if((macroInterface = MacroIndex.getJavaMacroInterface(project)) == null)
        {
            return result;
        }

        for(PsiClass javaMacro : ClassInheritorsSearch.INSTANCE.createQuery(new ClassInheritorsSearch.SearchParameters(macroInterface,
                allScope, true, true, false, new Condition<String>()
        {
            @Override
            public boolean value(String name)
            {
                return name.endsWith(MacroIndex.JAVA_MACRO_SUFFIX);
            }
        })).findAll())
        {
            if(javaMacro.isInterface() || javaMacro.getModifierList() == null
                    ||javaMacro.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT))
            {
                continue;
            }

            result.put((MacroIndex.macrosInDebugNamespace.contains(javaMacro.getName()) ? MacroIndex.DEBUG_NAMESPACE : MacroIndex.DEFAULT_NAMESPACE)
                    + "." + StringUtil.decapitalize(javaMacro.getName().replaceFirst(MacroIndex.JAVA_MACRO_SUFFIX + "$", "")), javaMacro);
        }

        return result;
    }

    @NotNull
    public static HashMultimap<String, JSElement> getJsMacros(@NotNull Project project,
                                                     @Nullable Collection<VirtualFile> restrictFiles)
    {
        return getJsSymbols(project, true, false, restrictFiles);
    }

    @NotNull
    public static HashMultimap<String, JSElement> getJsSymbols(@NotNull Project project, boolean macros, boolean namespaces,
                                                     @Nullable Collection<VirtualFile> restrictFiles)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();

        HashMultimap<String, JSElement> result = HashMultimap.create();

        for (String key : index.getAllKeys(JsMacroNameIndex.INDEX_ID, project))
        {
            MacroCollector<JSElement, JsMacroNameIndexedElement> collector = new JsMacroCollector(project);

            for (JsMacroNameIndexedElement macroName : index.getValues(JsMacroNameIndex.INDEX_ID, key, allScope))
            {
                if(macroName.isMacro() && macros || ! macroName.isMacro() && namespaces)
                {
                    for(VirtualFile file : restrictFiles != null ? restrictFiles :
                            index.getContainingFiles(JsMacroNameIndex.INDEX_ID, key, allScope))
                    {
                        index.processValues(JsMacroNameIndex.INDEX_ID, key, file, collector, allScope);
                    }
                }
            }

            for(JSElement element : collector.getResult())
            {
                result.put(key, element);
            }
        }

        return result;
    }

    @Nullable
    public static String getMacroFullyQualifienName(@NotNull final PsiElement macro)
    {
        CachedValue<String> cached = macro.getUserData(MACRO_QUALIFIED_NAME_KEY);

        if (cached == null)
        {
            cached = CachedValuesManager.getManager(macro.getProject()).createCachedValue(new CachedValueProvider<String>()
                {
                    @Nullable
                    @Override
                    public Result<String> compute()
                    {
                        String fullyQualifiedName = null;

                        if (macro instanceof PsiClass)
                        {
                            fullyQualifiedName = getJavaMacroFullyQualifienName((PsiClass) macro);
                        }
                        else if (macro instanceof JSProperty)
                        {
                            fullyQualifiedName = getJsMacroFullyQualifienName((JSProperty) macro);
                        }

                        return Result.create(fullyQualifiedName, macro);
                    }
                }, false);

            macro.putUserData(MACRO_QUALIFIED_NAME_KEY, cached);
        }

        return cached.getValue();
    }

    public static boolean isMacro(@NotNull PsiElement macro)
    {
        return getMacroFullyQualifienName(macro) != null;
    }

    @Nullable
    private static String getJavaMacroFullyQualifienName(@NotNull PsiClass macro)
    {
        for (Map.Entry<String, PsiClass> javaMacroEntry : getJavaMacros(macro.getProject()).entrySet())
        {
            if (javaMacroEntry.getValue().isEquivalentTo(macro))
            {
                return javaMacroEntry.getKey();
            }
        }

        return null;
    }

    @Nullable
    private static String getJsMacroFullyQualifienName(@NotNull JSProperty macro)
    {
        for (Map.Entry<String, JSElement> jsMacroEntry : getJsMacros(macro.getProject(), null).entries())
        {
            if (jsMacroEntry.getValue().isEquivalentTo(macro))
            {
                return jsMacroEntry.getKey();
            }
        }

        return null;
    }

}
