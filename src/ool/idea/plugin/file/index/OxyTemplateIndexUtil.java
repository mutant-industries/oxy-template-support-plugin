package ool.idea.plugin.file.index;

import com.intellij.codeInsight.completion.CompletionResultSet;
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
import java.util.List;
import ool.idea.plugin.editor.completion.lookupElement.BaseLookupElementProvider;
import ool.idea.plugin.file.index.collector.JavaMacroCollector;
import ool.idea.plugin.file.index.collector.JsMacroCollector;
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
    public static List<PsiElement> getMacroNameReferences(String macroName, @NotNull Project project)
    {
        List<PsiElement> references = new ArrayList<PsiElement>();
        PsiClass identifier;

        references.addAll(getJsMacroNameReferences(macroName, project));

        if((identifier = getJavaMacroNameReference(macroName, project)) != null)
        {
            references.add(identifier);
        }

        return references;
    }

    @Nullable
    public static PsiClass getJavaMacroNameReference(String macroName, @NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();
        JavaMacroCollector processor = new JavaMacroCollector(project);

        for(VirtualFile file : index.getContainingFiles(JavaMacroNameIndex.INDEX_ID,
                macroName, allScope))
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
    public static List<JSElement> getJsMacroNameReferences(String macroName, @NotNull final Project project)
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
    public static List<JSElement> getJsMacroNameReferences(String macroName, @NotNull VirtualFile file, @NotNull final Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();
        JsMacroCollector processor = new JsMacroCollector(project);

        index.processValues(JsMacroNameIndex.INDEX_ID, macroName, file, processor, allScope);

        return processor.getResult();
    }

    public static void addMacroNameCompletions(String namespace, @NotNull Project project, @NotNull CompletionResultSet resultSet,
                                               @NotNull BaseLookupElementProvider lookupElementProvider)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();

        for (String key : index.getAllKeys(JavaMacroNameIndex.INDEX_ID, project))
        {
            if ( ! key.startsWith(namespace))
            {
                continue;
            }

            JavaMacroCollector processor = new JavaMacroCollector(project);

            for(VirtualFile file : index.getContainingFiles(JavaMacroNameIndex.INDEX_ID, key, allScope))    // single iter
            {
                index.processValues(JavaMacroNameIndex.INDEX_ID, key, file, processor, allScope);
            }

            if(processor.getResult().size() == 1) // always true
            {
                resultSet.addElement(lookupElementProvider.create(key.replace(namespace, ""), processor.getResult().get(0)));
            }
        }

        for (String key : index.getAllKeys(JsMacroNameIndex.INDEX_ID, project))
        {
            String lookup = key.replace(namespace, "");
            JsMacroCollector processor = new JsMacroCollector(project);

            for (JsMacroNameIndexedElement macroName : index.getValues(JsMacroNameIndex.INDEX_ID, key, allScope))
            {
                if ( ! macroName.isMacro() || ! key.startsWith(namespace))
                {
                    continue;
                }

                for(VirtualFile file : index.getContainingFiles(JsMacroNameIndex.INDEX_ID, key, allScope))
                {
                    index.processValues(JsMacroNameIndex.INDEX_ID, key, file, processor, allScope);
                }
            }

            for(JSElement element : processor.getResult())
            {
                resultSet.addElement(lookupElementProvider.create(lookup, element));
            }
        }
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

    public static void addGlobalVariableCompletions(@NotNull Project project, @NotNull CompletionResultSet resultSet,
                                                    @NotNull BaseLookupElementProvider lookupElementProvider)
    {
        for (String key : FileBasedIndex.getInstance().getAllKeys(JsGlobalsIndex.INDEX_ID, project))
        {
            resultSet.addElement(lookupElementProvider.create(key, null));
        }
    }

}
