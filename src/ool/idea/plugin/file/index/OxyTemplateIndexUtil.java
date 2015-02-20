package ool.idea.plugin.file.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.collector.JsMacroCollector;
import ool.idea.plugin.file.index.collector.MacroCollector;
import ool.idea.plugin.file.index.globals.JsGlobalsIndex;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameDataIndexer;
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
    private static final Key<Multimap<String, JsMacroNameIndexedElement>> COMPILED_PREVIEW_MACRO_INDEX = Key.create("COMPILED_PREVIEW_MACRO_INDEX");

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

        return macroClass == null || macroClass.isInterface() || Arrays.asList(macroClass.getModifierList())
                .contains(PsiModifier.ABSTRACT) ? null : macroClass;
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
            if(javaMacro.isInterface() || Arrays.asList(javaMacro.getModifierList()).contains(PsiModifier.ABSTRACT))
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
    public static PsiElement getGlobalVariableRefrence(String variableName, @NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        List<Integer> key = FileBasedIndex.getInstance().getValues(JsGlobalsIndex.INDEX_ID, variableName, allScope);

        if ( ! key.isEmpty())
        {
            Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JsGlobalsIndex.INDEX_ID, variableName, allScope);

            if (files.isEmpty())
            {
                return null;    // no way
            }

            VirtualFile file = (VirtualFile) files.toArray()[0];
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if (psiFile != null)
            {
                PsiElement expr = psiFile.getViewProvider().findElementAt(key.get(0));

                if (expr != null && expr.getParent() instanceof PsiLiteralExpression)
                {
                    return expr.getParent();
                }
            }
        }

        return null;
    }

    // ------------------ compiled templates
    @NotNull
    public static List<JSElement> getJsMacroNameReferencesForCompiledTemplate(@NotNull String macroName, @NotNull PsiFile psiFile)
    {
        List<JSElement> result = new LinkedList<JSElement>();

        Multimap<String, JsMacroNameIndexedElement> index = psiFile.getUserData(COMPILED_PREVIEW_MACRO_INDEX);

        if(index == null)
        {
            return result;
        }

        for(JsMacroNameIndexedElement indexedElement : index.get(macroName))
        {
            PsiElement psiElement = psiFile.getViewProvider().findElementAt(indexedElement.getOffserInFile() - 1);

            if (psiElement != null && psiElement.getParent() instanceof JSElement)
            {
                result.add((JSElement) psiElement.getParent());
            }
        }

        return result;
    }

    public static void triggerReindexing(@NotNull PsiFile psiFile)
    {
        psiFile.putUserData(COMPILED_PREVIEW_MACRO_INDEX, new JsMacroNameDataIndexer().map(psiFile));
    }

}
