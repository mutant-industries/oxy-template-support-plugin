package ool.idea.plugin.file.index;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
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
    @Nullable
    public static PsiElement getMacroNameReference(String partialText, @NotNull Project project)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JavaMacroNameIndex.INDEX_ID,
                partialText, allScope);

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

        Collection<JsMacroNameIndexedElement> macros = FileBasedIndex.getInstance().getValues(JsMacroNameIndex.INDEX_ID,
                partialText, allScope);

        if(macros.size() > 1)
        {
            // TODO depends on include directives
        }
        else if(macros.size() > 0)
        {
            JsMacroNameIndexedElement macroName = macros.iterator().next();
            VirtualFile file = FileBasedIndex.getInstance().getContainingFiles(JsMacroNameIndex.INDEX_ID, partialText, allScope)
                    .iterator().next();

            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if(psiFile != null )
            {
                PsiElement psiElement = psiFile.getViewProvider().findElementAt(macroName.getOffserInFile());

                if(psiElement != null && psiElement.getParent() instanceof JSElement)
                {
                    return psiElement.getParent();
                }
            }
        }

        return null;
    }

    public static void addMacroNameCompletions(String partialText, @NotNull Project project, @NotNull CompletionResultSet resultSet,
                                               @NotNull BaseLookupElementProvider lookupElementProvider)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        for (String key : FileBasedIndex.getInstance().getAllKeys(JavaMacroNameIndex.INDEX_ID, project))
        {
            if (key.startsWith(partialText))
            {
                resultSet.addElement(lookupElementProvider.create(key.replace(partialText, "")));
            }
        }

        for (String key : FileBasedIndex.getInstance().getAllKeys(JsMacroNameIndex.INDEX_ID, project))
        {
            for (JsMacroNameIndexedElement macroName : FileBasedIndex.getInstance().getValues(JsMacroNameIndex.INDEX_ID, key, allScope))
            {
                if ( ! macroName.isMacro())
                {
                    continue;
                }

                if (key.startsWith(partialText))
                {
                    resultSet.addElement(lookupElementProvider.create(key.replace(partialText, "")));
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
