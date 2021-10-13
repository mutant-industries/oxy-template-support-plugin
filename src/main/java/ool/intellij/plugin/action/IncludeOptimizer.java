package ool.intellij.plugin.action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ool.intellij.plugin.editor.inspection.fix.MissingIncludeDirectiveQuickFix;
import ool.intellij.plugin.file.OxyTemplateFile;
import ool.intellij.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.intellij.plugin.psi.DirectiveStatement;
import ool.intellij.plugin.psi.OxyTemplateHelper;
import ool.template.core.impl.chunk.directive.IncludeOnceDirective;

import com.intellij.lang.ImportOptimizer;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 2/10/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class IncludeOptimizer implements ImportOptimizer
{
    private static final Pattern SUPPRESS_OPTIMIZER_DIRECTIVE = Pattern.compile("<//\\s*suppress-optimizer\\s*//>");

    @Override
    public boolean supports(@NotNull PsiFile file)
    {
        return file instanceof OxyTemplateFile;
    }

    @NotNull
    @Override
    public Runnable processFile(@NotNull PsiFile file)
    {
        return () ->
        {
            Map<PsiElement, JSElement> usedJsMacros = OxyTemplateHelper.getUsedJsMacros(file);

            PsiTreeUtil.getChildrenOfTypeAsList(file, DirectiveStatement.class).stream()
                    .filter(statement -> IncludeOnceDirective.NAME.equals(statement.getName()) && ! ignore(statement))
                    .forEach(PsiElement::delete);

            List<MissingDirectiveDescriptor> missingDirectiveDescriptors = new LinkedList<>();
            List<VirtualFile> includedFiles = new LinkedList<>();

            for (Map.Entry<PsiElement, JSElement> pair : usedJsMacros.entrySet())
            {
                final PsiElement macroCall = pair.getKey();
                final VirtualFile containingFile = pair.getValue().getContainingFile().getVirtualFile();

                if ( ! includedFiles.contains(containingFile) && ! containingFile.equals(file.getVirtualFile()))
                {
                    missingDirectiveDescriptors.add(new MissingDirectiveDescriptor(macroCall, pair.getValue()));

                    includedFiles.add(containingFile);
                }
            }

            Collections.sort(missingDirectiveDescriptors, (d1, d2) -> String.CASE_INSENSITIVE_ORDER.compare(d1.getReference()
                    .getContainingFile().getVirtualFile().getPath(), d2.getReference().getContainingFile().getVirtualFile().getPath()));

            for (MissingDirectiveDescriptor descriptor : missingDirectiveDescriptors)
            {
                new MissingIncludeDirectiveQuickFix(descriptor.getMacroCall(), descriptor.getReference(), IncludeOnceDirective.NAME)
                        .applyFix(file.getProject());
            }
        };
    }

    private static class MissingDirectiveDescriptor
    {
        private final PsiElement macroCall;
        private final PsiElement reference;

        public MissingDirectiveDescriptor(PsiElement macroCall, PsiElement reference)
        {
            this.macroCall = macroCall;
            this.reference = reference;
        }

        public PsiElement getMacroCall()
        {
            return macroCall;
        }

        public PsiElement getReference()
        {
            return reference;
        }

    }

    /**
     * <%@ include_once "file.jsm" %><// suppress-optimizer //>
     *
     * @param statement
     * @return true if statement is not to be touched by optimizer
     */
    public static boolean ignore(@NotNull DirectiveStatement statement)
    {
        PsiElement nextSibling = statement.getNextSibling();

        if (nextSibling instanceof PsiWhiteSpace)
        {
            nextSibling = nextSibling.getNextSibling();
        }

        return nextSibling != null && OxyTemplateParserDefinition.COMMENTS.contains(nextSibling.getNode().getElementType())
                && SUPPRESS_OPTIMIZER_DIRECTIVE.matcher(nextSibling.getText()).matches();
    }

}
