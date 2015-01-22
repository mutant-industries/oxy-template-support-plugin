package ool.idea.plugin.psi.impl;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.psi.DirectiveParamFileReference;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.MacroNameIdentifier;
import ool.idea.plugin.psi.OxyTemplateElementFactory;
import ool.idea.plugin.psi.reference.JavaMacroReference;
import ool.idea.plugin.psi.reference.JsMacroReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/16/15
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

    @Nullable
    public static PsiReference getReference(@NotNull MacroNameIdentifier macroNameIdentifier)
    {
        String partialText = macroNameIdentifier.getParent().getText().substring(0,
                macroNameIdentifier.getStartOffsetInParent() + macroNameIdentifier.getTextLength());

        List<JSElement> jsMacroReferences;
        PsiClass javaMacroReference;

        if((javaMacroReference = OxyTemplateIndexUtil.getJavaMacroNameReference(partialText,
                macroNameIdentifier.getProject())) != null)
        {
            return new JavaMacroReference(macroNameIdentifier, javaMacroReference);
        }
        else if((jsMacroReferences = OxyTemplateIndexUtil.getJsMacroNameReferences(partialText,
                macroNameIdentifier.getProject())).size() > 0)
        {
            return new JsMacroReference(macroNameIdentifier, jsMacroReferences
                    .toArray(new JSElement[jsMacroReferences.size()]));
        }

        return null;
    }

    public static PsiElement setName(MacroNameIdentifier macroNameIdentifier, String newName)
    {
        return macroNameIdentifier.replace(OxyTemplateElementFactory
                .createMacroNameIdentifier(macroNameIdentifier.getProject(), newName));
    }

    public static String getName(MacroNameIdentifier macroNameIdentifier)
    {
        return macroNameIdentifier.getText();
    }

    @NotNull
    public static Map<DirectiveParamFileReference, PsiFile> getIncludedFiles(@NotNull PsiFile psiFile)
    {
        IncludedFilesCollector collector = new IncludedFilesCollector();

        psiFile.acceptChildren(collector);

        return collector.getResult();
    }

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

                    if (macroName.getMacroFunction() != null && macroName.getMacroFunction().getReference() != null
                            && (reference = macroName.getMacroFunction().getReference().resolve()) instanceof JSProperty)
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
        if(macro.getContainingFile().getVirtualFile().getPath().equals(file.getVirtualFile().getPath()))
        {
            return false;
        }

        Iterator<Map.Entry<DirectiveParamFileReference, PsiFile>> iterator = OxyTemplatePsiUtil.getIncludedFiles(file).entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry<DirectiveParamFileReference, PsiFile> pair = iterator.next();

            PsiFile includedFile = pair.getValue();

            if(macro.getContainingFile().getVirtualFile().getPath().equals(includedFile.getVirtualFile().getPath()))
            {
                return false;
            }
        }

        return true;
    }

}
