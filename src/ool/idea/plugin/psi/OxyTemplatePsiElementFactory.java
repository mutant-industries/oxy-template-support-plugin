package ool.idea.plugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ool.idea.plugin.psi.impl.BlockCloseStatementImpl;
import ool.idea.plugin.psi.impl.BlockOpenStatementImpl;
import ool.idea.plugin.psi.impl.BlockStatementImpl;
import ool.idea.plugin.psi.impl.DirectiveOpenStatementImpl;
import ool.idea.plugin.psi.impl.DirectiveParamFileReferenceImpl;
import ool.idea.plugin.psi.impl.DirectiveParamWrapperImpl;
import ool.idea.plugin.psi.impl.DirectiveStatementImpl;
import ool.idea.plugin.psi.impl.MacroAttributeImpl;
import ool.idea.plugin.psi.impl.MacroExpressionParamImpl;
import ool.idea.plugin.psi.impl.MacroNameImpl;
import ool.idea.plugin.psi.impl.MacroParamImpl;
import ool.idea.plugin.psi.impl.MacroParamNameImpl;
import ool.idea.plugin.psi.impl.MacroTagImpl;
import ool.idea.plugin.psi.impl.MacroUnpairedTagImpl;
import ool.idea.plugin.psi.impl.MacroXmlPrefixImpl;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplatePsiElementFactory
{
    public static PsiElement createElement(ASTNode node)
    {
        IElementType type = node.getElementType();
        if (type == OxyTemplateTypes.BLOCK_CLOSE_STATEMENT)
        {
            return new BlockCloseStatementImpl(node);
        }
        else if (type == OxyTemplateTypes.BLOCK_OPEN_STATEMENT)
        {
            return new BlockOpenStatementImpl(node);
        }
        else if (type == OxyTemplateTypes.BLOCK_STATEMENT)
        {
            return new BlockStatementImpl(node);
        }
        else if (type == OxyTemplateTypes.DIRECTIVE_OPEN_STATEMENT)
        {
            return new DirectiveOpenStatementImpl(node);
        }
        else if (type == OxyTemplateTypes.DIRECTIVE_PARAM_FILE_REFERENCE)
        {
            return new DirectiveParamFileReferenceImpl(node);
        }
        else if (type == OxyTemplateTypes.DIRECTIVE_PARAM_WRAPPER)
        {
            return new DirectiveParamWrapperImpl(node);
        }
        else if (type == OxyTemplateTypes.DIRECTIVE_STATEMENT)
        {
            return new DirectiveStatementImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_ATTRIBUTE)
        {
            return new MacroAttributeImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_EXPRESSION_PARAM)
        {
            return new MacroExpressionParamImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_NAME)
        {
            return new MacroNameImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_PARAM)
        {
            return new MacroParamImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_PARAM_NAME)
        {
            return new MacroParamNameImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_TAG)
        {
            return new MacroTagImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_UNPAIRED_TAG)
        {
            return new MacroUnpairedTagImpl(node);
        }
        else if (type == OxyTemplateTypes.MACRO_XML_PREFIX)
        {
            return new MacroXmlPrefixImpl(node);
        }
        throw new AssertionError("Unknown element type: " + type);
    }

}
