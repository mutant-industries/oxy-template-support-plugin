package ool.idea.plugin.file.index;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ool.idea.plugin.editor.completion.lookupElement.BaseLookupElementProvider;
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
        PsiIdentifier identifier;

        references.addAll(getJsMacroNameReferences(macroName, project));

        if((identifier = getJavaMacroNameReference(macroName, project)) != null)
        {
            references.add(identifier);
        }

        return references;
    }

    @Nullable
    public static PsiIdentifier getJavaMacroNameReference(String macroName, @NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JavaMacroNameIndex.INDEX_ID,
                macroName, allScope);

        if( ! files.isEmpty())
        {
            VirtualFile file = (VirtualFile) files.toArray()[0];
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if(psiFile != null)
            {
                PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);

                if(psiClass != null && psiClass.getNameIdentifier() != null)
                {
                    return psiClass.getNameIdentifier();
                }
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
        final List<JSElement> results = new ArrayList<JSElement>();

        index.processValues(JsMacroNameIndex.INDEX_ID, macroName, file, new FileBasedIndex.ValueProcessor<JsMacroNameIndexedElement>()
        {
            @Override
            public boolean process(VirtualFile file, JsMacroNameIndexedElement macroNameElement)
            {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

                if(psiFile != null )
                {
                    PsiElement psiElement = psiFile.getViewProvider().findElementAt(macroNameElement.getOffserInFile() - 1);

                    if(psiElement != null && psiElement.getParent() instanceof JSElement)
                    {
                        results.add((JSElement) psiElement.getParent());
                    }
                }

                return true;
            }
        }, allScope);

        return results;
    }

    public static void addMacroNameCompletions(String namespace, @NotNull Project project, @NotNull CompletionResultSet resultSet,
                                               @NotNull BaseLookupElementProvider lookupElementProvider)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);
        FileBasedIndex index = FileBasedIndex.getInstance();

        for (String key : index.getAllKeys(JavaMacroNameIndex.INDEX_ID, project))
        {
            if (key.startsWith(namespace))
            {
                resultSet.addElement(lookupElementProvider.create(key.replace(namespace, "")));
            }
        }

        for (String key : index.getAllKeys(JsMacroNameIndex.INDEX_ID, project))
        {
            for (JsMacroNameIndexedElement macroName : index.getValues(JsMacroNameIndex.INDEX_ID, key, allScope))
            {
                if ( ! macroName.isMacro())
                {
                    continue;
                }

                if (key.startsWith(namespace))
                {
                    resultSet.addElement(lookupElementProvider.create(key.replace(namespace, "")));
                }
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
            resultSet.addElement(lookupElementProvider.create(key));
        }
    }

}
