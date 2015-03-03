package ool.idea.plugin.editor.format;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.xml.AbstractXmlBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.tree.IElementType;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateLanguageBlock;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.editor.format.builder.OxyTemplateInjectedBlockBuilder;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/13/15
 *
 * @see {@link com.intellij.psi.formatter.xml.XmlTagBlock}
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroTagBlock extends TemplateLanguageBlock
{
    private final AbstractTemplateLanguageFormattingModelBuilder builder;

    private final XmlFormattingPolicy xmlPolicy;

    private final CodeStyleSettings settings;

    private final OxyTemplateInjectedBlockBuilder injectedLanguageBlockBuilder;

    public MacroTagBlock(AbstractTemplateLanguageFormattingModelBuilder builder, ASTNode node, Wrap wrap,
                         Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy formattingPolicy, Indent indent)
    {
        super(builder, node, wrap, alignment, settings, formattingPolicy, indent);

        this.injectedLanguageBlockBuilder = new OxyTemplateInjectedBlockBuilder(formattingPolicy, builder);
        this.xmlPolicy = formattingPolicy;
        this.builder = builder;
        this.settings = settings;
    }

    @Override
    protected List<Block> buildChildren()
    {
        ASTNode child = myNode.getFirstChildNode();

        final Wrap tagBeginWrap = Wrap.createWrap(WrapType.NORMAL, true);
        final Wrap attrWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(xmlPolicy.getAttributesWrap()), false);
        final Alignment attrAlignment = Alignment.createAlignment();
        final Alignment textAlignment = Alignment.createAlignment();
        final ArrayList<Block> result = new ArrayList<Block>(3);
        ArrayList<Block> localResult = new ArrayList<Block>(1);

        boolean insideTag = true;

        while (child != null)
        {
            if ( ! FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0)
            {

                Wrap wrap = chooseWrap(child, tagBeginWrap, attrWrap);
                Alignment alignment = chooseAlignment(child, attrAlignment, textAlignment);

                if (child.getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_END
                        || child.getElementType() == OxyTemplateTypes.T_XML_OPEN_TAG_END)
                {
                    child = processChild(localResult, child, wrap, alignment, null);
                    result.add(createTagDescriptionNode(localResult));
                    localResult = new ArrayList<Block>(1);
                    insideTag = true;
                }
                else if (child.getElementType() == OxyTemplateTypes.T_XML_TAG_START)
                {
                    insideTag = false;

                    if (!localResult.isEmpty())
                    {
                        result.add(createTagContentNode(localResult));
                    }

                    localResult = new ArrayList<Block>(1);
                    child = processChild(localResult, child, wrap, alignment, null);
                }
                else if (child.getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START)
                {
                    insideTag = false;

                    if (!localResult.isEmpty())
                    {
                        result.add(createTagContentNode(localResult));
                        localResult = new ArrayList<Block>(1);
                    }

                    child = processChild(localResult, child, wrap, alignment, null);
                }
                else if (child.getElementType() == OxyTemplateTypes.T_XML_EMPTY_TAG_END)
                {
                    child = processChild(localResult, child, wrap, alignment, null);
                    result.add(createTagDescriptionNode(localResult));

                    localResult = new ArrayList<Block>(1);
                }
                else
                {
                    child = processChild(localResult, child, wrap, alignment,
                            insideTag ? getChildrenIndent() : null);
                }
            }
            if (child != null)
            {
                child = child.getTreeNext();
            }
        }

        if ( ! localResult.isEmpty())
        {
            result.add(createTagContentNode(localResult));
        }

        return result;
    }

    @Nullable
    protected ASTNode processChild(List<Block> result, final ASTNode child, final Wrap wrap, final Alignment alignment,
                                   final Indent indent)
    {
        final PsiElement childPsi = child.getPsi();

        if (childPsi.getNode().getElementType() == OxyTemplateTypes.T_TEMPLATE_HTML_CODE)
        {
            return injectedLanguageBlockBuilder.buildInjectedBlocks(result, child, indent,
                    ((MacroTag)getNode().getPsi()).getContentRange());
        }
        // TODO innerjs
        else
        {
            processSimpleChild(child, indent, result, wrap, alignment);
            return child;
        }
    }

    protected void processSimpleChild(final ASTNode child, final Indent indent, final List<Block> result, final Wrap wrap,
                                      final Alignment alignment)
    {
        if (child.getPsi() instanceof MacroCall)
        {
            MacroTagBlock tagBlock = new MacroTagBlock(builder, child, wrap, alignment, settings, xmlPolicy,
                    indent != null ? indent : Indent.getNoneIndent());

            result.add(tagBlock);
        }
        else
        {
            result.add(new OxyTemplateBlock(builder, child, wrap, alignment, settings, xmlPolicy, indent));
        }
    }

    protected Block createSyntheticBlock(final ArrayList<Block> localResult, final Indent childrenIndent)
    {
        return new OxyTemplateSyntheticBlock(localResult, Indent.getNoneIndent(), childrenIndent);
    }

    private Block createTagDescriptionNode(final ArrayList<Block> localResult)
    {
        return createSyntheticBlock(localResult, null);
    }

    private Block createTagContentNode(final ArrayList<Block> localResult)
    {
        return createSyntheticBlock(localResult, getChildrenIndent());
    }

    // ------------------------------------------------------------------------------------------------
    @NotNull
    public ChildAttributes getChildAttributes(final int newChildIndex)
    {
        return new ChildAttributes(Indent.getNormalIndent(), null);
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2)
    {
        return null;
    }

    @Nullable
    @Override
    protected Spacing getSpacing(TemplateLanguageBlock templateLanguageBlock)
    {
        return getSpacing(this, templateLanguageBlock);
    }

    @Nullable
    protected Wrap chooseWrap(final ASTNode child, final Wrap tagBeginWrap, final Wrap attrWrap)
    {
        final IElementType elementType = child.getElementType();

        if (elementType == OxyTemplateTypes.MACRO_ATTRIBUTE)
        {
            return attrWrap;
        }
        if (elementType == OxyTemplateTypes.T_XML_TAG_START)
        {
            return tagBeginWrap;
        }
        if (elementType == OxyTemplateTypes.T_XML_CLOSE_TAG_START)
        {
            final PsiElement parent = SourceTreeToPsiMap.treeElementToPsi(child.getTreeParent());

            if (parent instanceof MacroTag)
            {
                final MacroTag tag = (MacroTag) parent;
                if (tag.getMacroTagList().size() > 0)
                {
                    return Wrap.createWrap(tag.getMacroTagList().size() > 0 ? WrapType.ALWAYS : WrapType.NORMAL, true);
                }
            }
        }

        return null;
    }

    @Nullable
    protected Alignment chooseAlignment(final ASTNode child, final Alignment attrAlignment, final Alignment textAlignment)
    {
        final IElementType elementType = child.getElementType();

        if (elementType == OxyTemplateTypes.MACRO_ATTRIBUTE && xmlPolicy.getShouldAlignAttributes())
        {
            return attrAlignment;
        }
        if (elementType == OxyTemplateTypes.T_TEMPLATE_HTML_CODE && xmlPolicy.getShouldAlignText())
        {
            return textAlignment;
        }

        return null;
    }

    @NotNull
    protected Indent getChildrenIndent()
    {
        return getNode().getPsi().getFirstChild() instanceof PsiErrorElement ?
                Indent.getNoneIndent() : Indent.getIndent(Indent.Type.NORMAL, false, true);
    }

    @NotNull
    @Override
    protected Indent getChildIndent(@NotNull ASTNode astNode)
    {
        return Indent.getNoneIndent();  // never called anyway
    }

}
