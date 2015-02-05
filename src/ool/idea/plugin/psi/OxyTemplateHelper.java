package ool.idea.plugin.psi;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSRecursiveElementVisitor;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.collector.IncludedFilesCollector;
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
    public static Map<PsiElement, JSProperty> getUsedJsMacros(@NotNull PsiFile psiFile)
    {
        final Map<PsiElement, JSProperty> usedMacros = new HashMap<PsiElement, JSProperty>();

        new MacroNameVisitor()
        {
            @Override
            public void visitMacroName(@NotNull MacroName macroName)
            {
                PsiElement reference;

                // TODO macros.oxy.neco7.neco8 = function (params) {... - JSDefinitionExpression
                if (macroName.getReference() != null
                        && (reference = macroName.getReference().resolve()) instanceof JSProperty)
                {
                    usedMacros.put(macroName, (JSProperty) reference);
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

                if (referenceExpression.getReference() != null &&
                        (reference = referenceExpression.getReference().resolve()) instanceof JSProperty)
                {
                    usedMacros.put(referenceExpression, (JSProperty) reference);
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

}
