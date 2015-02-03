package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.insert.IncludeAutoInsertHandler;
import ool.idea.plugin.editor.completion.insert.TrailingPatternConsumer;
import ool.idea.plugin.file.OxyTemplateFileType;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateTypes;
import ool.idea.plugin.psi.impl.OxyTemplatePsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroName extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        MacroName elementAt;

        if(parameters.getPosition().getNode().getElementType() != OxyTemplateTypes.T_MACRO_NAME
                || (elementAt= PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroName.class)) == null
                || elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() != OxyTemplateTypes.T_XML_TAG_START)
        {
            return;
        }

        Collection<VirtualFile> restriction = null;

        if(parameters.getInvocationCount() == 1)
        {
            final String shortcut = getActionShortcut(IdeActions.ACTION_CODE_COMPLETION);
            result.addLookupAdvertisement("Press " + shortcut + " again to search for all matching macros");

            restriction = OxyTemplatePsiUtil.getIncludedFiles(parameters.getOriginalFile()).values();
        }

        for(Map.Entry<String, PsiElement> entry : OxyTemplateIndexUtil.getMacros(elementAt.getProject(),
                restriction).entrySet())
        {
            String key = entry.getKey();
            PsiElement element = entry.getValue();

            int namespaceEnd = key.lastIndexOf('.');
            String macroNamespace = key.substring(0, namespaceEnd);
            String macroName = key.substring(namespaceEnd + 1);

            result.withPrefixMatcher(new CamelHumpMatcher(elementAt.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")))
                .consume(LookupElementDecorator.withInsertHandler(LookupElementBuilder
                    .create(element, key)
                    .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                    .withPresentableText(macroName + " (" + macroNamespace + ")")
                    .withTailText(" " + element.getContainingFile().getVirtualFile().getPath().replaceFirst("^.+((src)|(WEB_INF))/", ""), true)
                    .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE),
                new IncludeAutoInsertHandler()));
        }
    }

}
