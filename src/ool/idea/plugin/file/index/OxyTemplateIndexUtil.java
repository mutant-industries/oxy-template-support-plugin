package ool.idea.plugin.file.index;

import com.google.common.collect.HashMultimap;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.collector.JavaMacroCollector;
import ool.idea.plugin.file.index.collector.JsMacroCollector;
import ool.idea.plugin.file.index.collector.MacroCollector;
import ool.idea.plugin.file.index.globals.JsGlobalsIndex;
import ool.idea.plugin.file.index.nacros.java.JavaMacroNameIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameIndexedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateIndexUtil
{
    @NotNull
    public static List<PsiElement> getMacroNameReferences(@NotNull String macroName, @NotNull Project project)
    {
        List<PsiElement> references = new ArrayList<PsiElement>();
        PsiClass psiClass;

        references.addAll(getJsMacroNameReferences(macroName, project));

        if((psiClass = getJavaMacroNameReference(macroName, project)) != null)
        {
            references.add(psiClass);
        }

        return references;
    }

    @Nullable
    public static PsiClass getJavaMacroNameReference(@NotNull String macroName, @NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();
        JavaMacroCollector processor = new JavaMacroCollector(project);

        for(VirtualFile file : index.getContainingFiles(JavaMacroNameIndex.INDEX_ID,
                macroName, allScope))   // single iteration
        {
            index.processValues(JavaMacroNameIndex.INDEX_ID, macroName, file, processor, allScope);

            if(processor.getResult().size() == 1) // always true
            {
                return processor.getResult().get(0);
            }
        }

        return null;
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
        FileBasedIndex index = FileBasedIndex.getInstance();

        Map<String, PsiClass> result = new HashMap<String, PsiClass>();

        for (String key : index.getAllKeys(JavaMacroNameIndex.INDEX_ID, project))
        {
            JavaMacroCollector processor = new JavaMacroCollector(project);

            for(VirtualFile file : index.getContainingFiles(JavaMacroNameIndex.INDEX_ID, key, allScope))    // single iter
            {
                index.processValues(JavaMacroNameIndex.INDEX_ID, key, file, processor, allScope);
            }

            if(processor.getResult().size() == 1) // always true
            {
                result.put(key, processor.getResult().get(0));
            }
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

}
