package ool.idea.plugin.psi.impl;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import java.util.HashMap;
import java.util.Map;
import ool.idea.plugin.file.index.collector.IncludedFilesCollector;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.DirectiveStatement;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.reference.MacroReferenceSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
 * TODO split grammar related x rest
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplatePsiUtil
{
    @NotNull
    public static PsiReference[] getReferences(@NotNull DirectiveParamFileReference directiveParamFileReference)
    {
        return new FileReferenceSet(directiveParamFileReference).getAllReferences();
    }

    @NotNull
    public static PsiReference[] getReferences(@NotNull MacroName macroName)
    {
        return new MacroReferenceSet(macroName).getAllReferences();
    }

    @Nullable
    public static PsiReference getReference(@NotNull MacroName macroName)
    {
        PsiReference[] references = macroName.getReferences();

        return references.length > 0 ? references[0] : null;
    }

    public static PsiElement setName(MacroName macroName, String newName)
    {
        return macroName.replace(OxyTemplateElementFactory.createMacroName(macroName.getProject(), newName));
    }

    public static String getName(MacroName macroName)
    {
        return macroName.getText();
    }

    public static String getType(DirectiveStatement directiveStatement)
    {
        PsiElement psiElement = directiveStatement.getDirectiveOpenStatement().getNextSibling();

        if(psiElement instanceof PsiWhiteSpace)
        {
            psiElement = psiElement.getNextSibling();
        }

        return psiElement.getText();
    }

    @NotNull
    public static Map<DirectiveParamFileReference, VirtualFile> getIncludedFiles(@NotNull PsiFile psiFile)
    {
        IncludedFilesCollector collector = new IncludedFilesCollector();

        psiFile.getViewProvider().getPsi(OxyTemplate.INSTANCE).acceptChildren(collector);

        return collector.getResult();
    }

    @NotNull
    public static Map<PsiElement, JSProperty> getUsedJsMacros(@NotNull PsiFile psiFile)
    {
        final Map<PsiElement, JSProperty> usedMacros = new HashMap<PsiElement, JSProperty>();

        psiFile.getViewProvider().getPsi(OxyTemplate.INSTANCE).acceptChildren(new PsiRecursiveElementVisitor()
        {
            @Override
            public void visitElement(PsiElement element)
            {
                if ((element instanceof MacroName))
                {
                    MacroName macroName = (MacroName) element;
                    PsiElement reference;

                    // TODO macros.oxy.neco7.neco8 = function (params) {... - JSDefinitionExpression
                    if (macroName.getReference() != null
                            && (reference = macroName.getReference().resolve()) instanceof JSProperty)
                    {
                        usedMacros.put(macroName, (JSProperty) reference);
                    }
                }

                super.visitElement(element);
            }
        });

        psiFile.getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE).acceptChildren(new PsiRecursiveElementVisitor()
        {
            @Override
            public void visitElement(PsiElement element)
            {
                if ((element instanceof JSReferenceExpression))
                {
                    JSReferenceExpression referenceExpression = (JSReferenceExpression) element;
                    PsiElement reference;

                    if (referenceExpression.getParent() instanceof JSCallExpression && referenceExpression.getReference() != null &&
                            (reference = referenceExpression.getReference().resolve()) instanceof JSProperty)
                    {
                        usedMacros.put(referenceExpression, (JSProperty) reference);
                    }
                }

                super.visitElement(element);
            }
        });

        return usedMacros;
    }

    public static boolean isJsMacroMissingInclude(@NotNull PsiFile file, @NotNull PsiElement macro)
    {
        if(macro.getContainingFile().getVirtualFile().equals(file.getVirtualFile()))
        {
            return false;
        }

        return ! OxyTemplatePsiUtil.getIncludedFiles(file).values().contains(macro.getContainingFile().getVirtualFile());
    }

}
