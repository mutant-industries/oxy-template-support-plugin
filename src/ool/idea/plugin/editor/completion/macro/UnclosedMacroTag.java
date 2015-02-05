package ool.idea.plugin.editor.completion.macro;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.handler.TrailingPatternConsumer;
import ool.idea.plugin.psi.MacroName;
import ool.idea.plugin.psi.OxyTemplateHelper;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class UnclosedMacroTag extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*>");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        MacroName elementAt;

        if(parameters.getPosition().getNode().getElementType() != OxyTemplateTypes.T_MACRO_NAME
                || (elementAt = PsiTreeUtil.getParentOfType(parameters.getPosition(), MacroName.class)) == null
                || elementAt.getPrevSibling().getPrevSibling().getNode().getElementType() != OxyTemplateTypes.T_XML_CLOSE_TAG_START)
        {
            return;
        }

        String macroTagToBeClosedName = OxyTemplateHelper.getPreviousUnclosedMacroTagName(elementAt.getPrevSibling());

        if (macroTagToBeClosedName != null)
        {
            result.withPrefixMatcher(new CamelHumpMatcher(elementAt.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")))
                .consume(LookupElementBuilder.create(macroTagToBeClosedName + ">")
                .withPresentableText("m:" + macroTagToBeClosedName)
                .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME))
                .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE));
        }
    }

}
