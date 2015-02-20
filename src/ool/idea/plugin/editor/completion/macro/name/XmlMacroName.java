package ool.idea.plugin.editor.completion.macro.name;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.handler.IncludeAutoInsert;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class XmlMacroName extends AbstractMacroName
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*");

    @Override
    protected boolean accept(@NotNull PsiElement position)
    {
        MacroName elementAt;

        return position.getNode().getElementType() == OxyTemplateTypes.T_MACRO_NAME
                && (elementAt = PsiTreeUtil.getParentOfType(position, MacroName.class)) != null
                && elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() == OxyTemplateTypes.T_XML_TAG_START;
    }

    @Override
    public boolean addMacroNameCompletionVariant(@NotNull CompletionResultSet result, @NotNull PsiElement position, @NotNull PsiElement lookupElement,
                                                       @NotNull String fqn, @NotNull String macroName, @NotNull String macroNamespace)
    {
        result.withPrefixMatcher(new CamelHumpMatcher(position.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")))
            .consume(LookupElementDecorator.withInsertHandler(LookupElementBuilder
                .create(lookupElement, fqn)
                .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                .withPresentableText(macroName + " (" + macroNamespace + ")")
                .withTailText(" " + lookupElement.getContainingFile().getVirtualFile().getPath().replaceFirst("^.+((src)|(WEB_INF))/", ""), true)
                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE),
            new IncludeAutoInsert()));

        return true;
    }

}
