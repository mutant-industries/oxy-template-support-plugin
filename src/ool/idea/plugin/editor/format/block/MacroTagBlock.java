package ool.idea.plugin.editor.format.block;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.xml.AbstractXmlBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.tree.IElementType;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import java.util.ArrayList;
import java.util.List;
import ool.idea.plugin.psi.MacroCall;
import ool.idea.plugin.psi.MacroTag;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 * @see {@link com.intellij.psi.formatter.xml.XmlTagBlock}
 */
public class MacroTagBlock extends OxyTemplateForeignElementWrapper
{
    public MacroTagBlock(@NotNull AbstractTemplateLanguageFormattingModelBuilder builder, @NotNull ASTNode node, @Nullable Wrap wrap,
                         @Nullable Alignment alignment, CodeStyleSettings settings, XmlFormattingPolicy formattingPolicy, @Nullable Indent indent)
    {
        super(builder, node, wrap, alignment, settings, formattingPolicy, indent);

        assert node.getPsi() instanceof MacroCall;
    }

    @Override
    @NotNull
    public List<Block> buildChildren()
    {
        ASTNode child = myNode.getFirstChildNode();

        final Wrap tagBeginWrap = Wrap.createWrap(WrapType.NORMAL, true);
        final Wrap attrWrap = Wrap.createWrap(AbstractXmlBlock.getWrapType(xmlPolicy.getAttributesWrap()), false);
        final Alignment attrAlignment = Alignment.createAlignment();
        final Alignment textAlignment = Alignment.createAlignment();
        final ArrayList<Block> result = new ArrayList<Block>(5);
        ArrayList<Block> localResult = new ArrayList<Block>(10);

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

                    if ( ! localResult.isEmpty())
                    {
                        result.add(createTagContentNode(localResult));
                    }

                    localResult = new ArrayList<Block>(1);
                    child = processChild(localResult, child, wrap, alignment, null);
                }
                else if (child.getElementType() == OxyTemplateTypes.T_XML_CLOSE_TAG_START)
                {
                    insideTag = false;

                    if ( ! localResult.isEmpty())
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

            child = nextNode(child);
        }

        if ( ! localResult.isEmpty())
        {
            result.add(createTagContentNode(localResult));
        }

        return result;
    }

    @NotNull
    protected Block createTagDescriptionNode(@NotNull final ArrayList<Block> localResult)
    {
        return createSyntheticBlock(localResult, null);
    }

    @NotNull
    protected Block createTagContentNode(@NotNull final ArrayList<Block> localResult)
    {
        return createSyntheticBlock(localResult, getChildrenIndent());
    }

    @NotNull
    @Override
    protected TextRange getContentRange()
    {
        if(getNode().getPsi() instanceof MacroTag)
        {
            return ((MacroTag) getNode().getPsi()).getContentRange();
        }

        return super.getContentRange();
    }

    // ------------------------------------------------------------------------------------------------

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
                    return Wrap.createWrap(WrapType.ALWAYS, true);
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

        return super.chooseAlignment(child, textAlignment);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newChildIndex)
    {
        return new ChildAttributes(Indent.getNormalIndent(), null);
    }

    @Nullable
    @Override
    public Indent getIndent()
    {
        return indent;
    }

    @NotNull
    protected Indent getChildrenIndent()
    {
        return Indent.getIndent(Indent.Type.NORMAL, false, true);
    }

    @Override
    public boolean isIncomplete()
    {
        return getNode().getLastChildNode().getPsi() instanceof PsiErrorElement;
    }

}
