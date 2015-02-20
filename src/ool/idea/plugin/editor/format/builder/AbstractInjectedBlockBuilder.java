package ool.idea.plugin.editor.format.builder;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.xml.AnotherLanguageBlockWrapper;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/19/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class AbstractInjectedBlockBuilder
{
    protected final XmlFormattingPolicy policy;

    protected final AbstractTemplateLanguageFormattingModelBuilder builder;

    public AbstractInjectedBlockBuilder(@NotNull final AbstractTemplateLanguageFormattingModelBuilder builder,
                                        @NotNull final XmlFormattingPolicy policy)
    {
        this.builder = builder;
        this.policy = policy;
    }

    @NotNull
    abstract protected Language getInjectedElementsLanguage();

    @NotNull
    abstract protected IElementType getOuterLanguageElement();

    @Nullable
    abstract protected ASTNode shiftOriginalNode(@NotNull ASTNode originalNode, @NotNull List<Block> result);

    @NotNull
    abstract public Block createInjectedBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                                              @Nullable Indent indent, @NotNull TextRange range);

    @NotNull
    public Block createInjectedBlock(ASTNode node, Block originalBlock, Indent indent, int offset, TextRange range)
    {
        return new AnotherLanguageBlockWrapper(node, policy, originalBlock, indent, offset, range);
    }

    @NotNull
    public CodeStyleSettings getSettings()
    {
        return policy.getSettings();
    }

    @Nullable
    public ASTNode buildInjectedBlocks(@NotNull List<Block> result, @NotNull ASTNode injectedNode, @NotNull Indent indent,
                                       @NotNull TextRange allowedRange)
    {
        FileViewProvider viewProvider = injectedNode.getPsi().getContainingFile().getViewProvider();

        PsiElement elementAt = viewProvider.findElementAt(injectedNode.getStartOffset(), getInjectedElementsLanguage());
        List<Block> localResult = new ArrayList<Block>(5);

        while (elementAt != null)
        {
            if(elementAt.getNode().getElementType() == getOuterLanguageElement())
            {
                break;
            }
            else if ( ! allowedRange.contains(elementAt.getTextRange()))
            {
                /**
                 * if(elementAt instanceof XmlTag) -> html tag starts in macro tag but ends elsewhere
                 *  - applies only for OxyTemplateInjectedBlockBuilder
                 */
                if(allowedRange.getEndOffset() <= elementAt.getNode().getStartOffset() + elementAt.getTextLength()
                        && ! (elementAt instanceof XmlTag))
                {
                    break;
                }
            }

            /**
             * shift to topmost coverable element, e.g.:
             * ...html...<m:foo... - T_XML_TAG_START -> MACRO_[UNPAIRED_]TAG
             */
            if(allowedRange.contains(elementAt.getParent().getTextRange()))
            {
                elementAt = elementAt.getParent();
            }

            if ( ! FormatterUtil.containsWhiteSpacesOnly(elementAt.getNode()))
            {
                TextRange intersection = elementAt.getNode().getTextRange().intersection(allowedRange);

                localResult.add(createInjectedBlock(elementAt.getNode(), null, null, indent, intersection));
            }

            // turns up actually only in OxyTemplateInjectedBlockBuilder
            if (elementAt.getNextSibling() == null
                    && elementAt.getParent() instanceof XmlText)
            {
                elementAt = elementAt.getParent();
            }

            elementAt = elementAt.getNextSibling();
        }

        if (localResult.size() > 0)
        {
            for(Block blokk : localResult)
            {
                result.add(blokk);
            }

            return shiftOriginalNode(injectedNode, localResult);
        }

        return injectedNode;
    }

}
