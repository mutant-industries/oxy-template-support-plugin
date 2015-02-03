package ool.idea.plugin.editor.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import ool.idea.plugin.editor.completion.insert.IncludeAutoInsertHandler;
import ool.idea.plugin.editor.completion.insert.TrailingPatternConsumer;
import ool.idea.plugin.file.OxyTemplateFileType;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.psi.impl.OxyTemplatePsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 1/14/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroName extends CompletionContributor
{
    private static final Pattern INSERT_CONSUME = Pattern.compile("\\);[A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*\\(");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result)
    {
        PsiElement elementAt = parameters.getPosition(), prevSibling;
        JSReferenceExpression topReference;

        if(elementAt.getNode().getElementType() != JSTokenTypes.IDENTIFIER
                || (prevSibling = elementAt.getPrevSibling()) == null
                || prevSibling.getNode().getElementType() != JSTokenTypes.DOT
                || (topReference = PsiTreeUtil.getTopmostParentOfType(elementAt, JSReferenceExpression.class)) == null)
        {
            return;
        }

        String partialText = topReference.getText().substring(0, elementAt.getStartOffsetInParent());

        Collection<VirtualFile> restriction = null;

        if(parameters.getInvocationCount() == 1)
        {
            final String shortcut = getActionShortcut(IdeActions.ACTION_CODE_COMPLETION);
            result.addLookupAdvertisement("Press " + shortcut + " again to search for all matching macros");

            restriction = OxyTemplatePsiUtil.getIncludedFiles(parameters.getOriginalFile()).values();
        }

        Map<String, PsiElement> completions = new HashMap<String, PsiElement>();
        boolean inMacroNamespace = false;

        completions.putAll(OxyTemplateIndexUtil.getMacros(elementAt.getProject(), restriction));

        for(Map.Entry<String, PsiElement> entry : completions.entrySet())
        {
            String key = entry.getKey();
            PsiElement element = entry.getValue();

            String lookupString = key + "();";

            int namespaceEnd = key.lastIndexOf('.');
            String macroNamespace = key.substring(0, namespaceEnd);
            String macroName = key.substring(namespaceEnd + 1);

            result.withPrefixMatcher(new CamelHumpMatcher(partialText + elementAt.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "")))
                .consume(LookupElementDecorator.withInsertHandler(LookupElementBuilder
                    .create(element, lookupString)
                    .withIcon(OxyTemplateFileType.INSTANCE.getIcon())
                    .withPresentableText(macroName + "([Object] params) (" + macroNamespace + ")")
                    .withTailText(" " + element.getContainingFile().getVirtualFile().getPath().replaceFirst("^.+((src)|(WEB_INF))/", ""), true)
                    .withInsertHandler(new TrailingPatternConsumer(INSERT_CONSUME)
                    {
                        @Override
                        public void handleInsert(InsertionContext context, LookupElement item)
                        {
                            CaretModel caretModel = context.getEditor().getCaretModel();
                            caretModel.moveToOffset(caretModel.getOffset() - 2);

                            super.handleInsert(context, item);
                        }
                    })
                    .withAutoCompletionPolicy(AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE),
                new IncludeAutoInsertHandler()));

            inMacroNamespace = true;
        }

        if(inMacroNamespace)
        {
            result.stopHere();
        }
    }

}
