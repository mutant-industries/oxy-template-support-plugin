package ool.idea.plugin.psi.reference;

import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.Collection;
import java.util.List;
import ool.idea.plugin.file.index.JavaMacroNameIndex;
import ool.idea.plugin.file.index.JsGlobalsIndex;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateReferenceExpressionResolver extends JSReferenceExpressionResolver
{
    public OxyTemplateReferenceExpressionResolver(JSReferenceExpressionImpl expression, PsiFile file)
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

        PsiElement reference;

        String text = myRef.getElement().getText();

        if(text.startsWith("oxy.") && text.replace("oxy.", "").equals(myReferencedName))
        {
            // oxy namespace
            if((reference = getJavaMacroReference(myContainingFile.getProject(), myReferencedName)) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        ResolveResult[] parentResult =  super.doResolve();

        if((parentResult == null || parentResult.length == 0) && text.equals(myReferencedName))
        {
            // global
            if((reference = getGlobalVariableRefrence(myContainingFile.getProject(), myReferencedName)) != null)
            {
                return new JSResolveResult[]{new JSResolveResult(reference)};
            }
        }

        return parentResult;
    }

    private static PsiElement getJavaMacroReference(Project project, String macroName)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JavaMacroNameIndex.INDEX_ID, macroName, allScope);

        if( ! files.isEmpty())
        {
            VirtualFile file = (VirtualFile) files.toArray()[0];
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if(psiFile != null)
            {
                PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);

                if(psiClass != null)
                {
                    return psiClass.getNameIdentifier();
                }
            }
        }

        return null;
    }

    private static PsiElement getGlobalVariableRefrence(@NotNull Project project, String variableName)
    {
        final GlobalSearchScope allScope = ProjectScope.getProjectScope(project);

        List<Integer> key = FileBasedIndex.getInstance().getValues(JsGlobalsIndex.INDEX_ID, variableName, allScope);

        if( ! key.isEmpty())
        {
            Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(JsGlobalsIndex.INDEX_ID, variableName, allScope);

            if(files.isEmpty())
            {
                return null;    // no way
            }

            VirtualFile file = (VirtualFile) files.toArray()[0];
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if(psiFile != null)
            {
                PsiElement expr = psiFile.getViewProvider().findElementAt(key.get(0));

                if(expr != null && expr.getParent() instanceof PsiLiteralExpression)
                {
                    return expr.getParent();
                }
            }
        }

        return null;
    }

}
