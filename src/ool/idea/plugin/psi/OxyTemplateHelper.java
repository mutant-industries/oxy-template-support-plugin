package ool.idea.plugin.psi;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSRecursiveElementVisitor;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.file.index.collector.IncludedFilesCollector;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.psi.visitor.MacroNameVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateHelper
{
    @NotNull
    public static Map<DirectiveParamFileReference, VirtualFile> getIncludedFiles(@NotNull PsiFile psiFile)
    {
        IncludedFilesCollector collector = new IncludedFilesCollector();

        return collector.collect(psiFile).getResult();
    }

    @NotNull
    public static Map<PsiElement, JSElement> getUsedJsMacros(@NotNull PsiFile psiFile)
    {
        final Map<PsiElement, JSElement> usedMacros = new HashMap<PsiElement, JSElement>();

        new MacroNameVisitor()
        {
            @Override
            public void visitMacroName(@NotNull MacroName macroName)
            {
                PsiElement reference;

                if (macroName.getReference() != null && (reference = macroName.getReference().resolve()) != null
                        && OxyTemplateIndexUtil.getJsMacroNameReferences(macroName.getName(), macroName.getProject()).size() > 0)
                {
                    usedMacros.put(macroName, (JSElement) reference);
                }
            }
        }.visitFile(psiFile);

        new JSRecursiveElementVisitor()
        {
            @Override
            public void visitJSCallExpression(@NotNull JSCallExpression node)
            {
                if( ! (node.getFirstChild() instanceof JSReferenceExpression))
                {
                            return;
                }

                JSReferenceExpression referenceExpression = (JSReferenceExpression) node.getFirstChild();
                PsiElement reference;

                if (referenceExpression.getReference() != null && (reference = referenceExpression.getReference().resolve())  != null
                        && OxyTemplateIndexUtil.getJsMacroNameReferences(MacroIndex.normalizeMacroName(referenceExpression
                            .getText()), referenceExpression.getProject()).size() > 0)
                {
                    usedMacros.put(referenceExpression, (JSElement) reference);
                }
            }
        }.visitFile(psiFile.getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE));

        return usedMacros;
    }

    public static boolean isJsMacroMissingInclude(@NotNull PsiFile file, @NotNull PsiElement macro)
    {
        return ! macro.getContainingFile().getVirtualFile().equals(file.getVirtualFile())
                && ! getIncludedFiles(file).values().contains(macro.getContainingFile().getVirtualFile());
    }

    /**
     * @param elementAt anything between opening and closing tag name
     * @return
     */
    @Nullable
    public static String getPreviousUnclosedMacroTagName(@Nullable final PsiElement elementAt)
    {
        if(elementAt == null)
        {
            return null;
        }

        PsiElement psiElement = elementAt;

        do
        {
            if(psiElement instanceof MacroName)
            {
                String name = psiElement.getText();

                psiElement = elementAt;

                while((psiElement = psiElement.getNextSibling()) != null)
                {
                    if(psiElement instanceof MacroName)
                    {
                        if(psiElement.getText().equals(name))
                        {
                            if((psiElement = PsiTreeUtil.getTopmostParentOfType(elementAt, MacroTag.class)) != null
                                    && ((MacroTag) psiElement).getMacroNameList().size() == 1)
                            {
                                return name;
                            }

                            return null;
                        }

                        return name;
                    }
                }

                return name;
            }
        }
        while((psiElement = psiElement.getPrevSibling()) != null);

        return null;
    }

    public static void addDirective(@NotNull DirectiveStatement directiveStatement, @NotNull PsiFile file)
    {
        List<DirectiveStatement> statements = PsiTreeUtil.getChildrenOfTypeAsList(file, DirectiveStatement.class);

        if(statements.size() > 0)
        {
            file.addAfter(directiveStatement, statements.get(statements.size() - 1));
        }
        else
        {
            file.addBefore(directiveStatement, file.getFirstChild());
        }
    }

    @Nullable
    public static ResolveResult multiResolveWithIncludeSearch(@NotNull PsiElement referencing, @NotNull ResolveResult[] references)
    {
        Collection<VirtualFile> includedFiles = OxyTemplateHelper.getIncludedFiles(referencing
                .getContainingFile()).values();

        for(ResolveResult result : references)
        {
            if(includedFiles.contains(result.getElement().getContainingFile().getVirtualFile()))
            {
                return result;
            }
        }

        return null;
    }

}
