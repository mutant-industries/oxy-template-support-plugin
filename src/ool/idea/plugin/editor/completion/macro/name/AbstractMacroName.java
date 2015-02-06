package ool.idea.plugin.editor.completion.macro.name;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.I18nSupport;
import ool.idea.plugin.psi.OxyTemplateHelper;
import org.jetbrains.annotations.NotNull;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractMacroName extends CompletionContributor
{
    /**
     *
     * @param position
     * @return Is completion accepted at position?
     */
    abstract protected boolean accept(@NotNull PsiElement position);

    /**
     *
     * @param result
     * @param position
     * @param lookupElement
     * @param fqn
     * @param macroName
     * @param macroNamespace
     * @return Variant is matching given prefix ~ is in macro namespace
     */
    abstract public boolean addMacroNameCompletionVariant(@NotNull CompletionResultSet result, @NotNull PsiElement position, @NotNull PsiElement lookupElement,
                                                          @NotNull String fqn, @NotNull String macroName, @NotNull String macroNamespace);

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement position = parameters.getPosition();

        if( ! accept(position))
        {
            return;
        }

        Collection<VirtualFile> restriction = null;
        boolean inMacroNamespace = false;

        if (parameters.getInvocationCount() == 1)
        {
            result.addLookupAdvertisement(I18nSupport.message("completion.macro.name.advertisment",
                    getActionShortcut(IdeActions.ACTION_CODE_COMPLETION)));

            restriction = new LinkedList<VirtualFile>(OxyTemplateHelper.getIncludedFiles(parameters.getOriginalFile()).values());
            restriction.add(parameters.getOriginalFile().getVirtualFile());
        }

        for (Map.Entry<String, PsiElement> entry : OxyTemplateIndexUtil.getMacros(position.getProject(),
                restriction).entries())
        {
            String fqn = entry.getKey();
            int namespaceEnd = fqn.lastIndexOf('.');

            inMacroNamespace |= addMacroNameCompletionVariant(result, position, entry.getValue(), fqn, fqn.substring(namespaceEnd + 1),
                    fqn.substring(0, namespaceEnd));
        }

        if(inMacroNamespace)
        {
            result.stopHere();
        }
    }

}
