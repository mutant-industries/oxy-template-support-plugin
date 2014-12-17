package ool.idea.macro.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ool.idea.macro.psi.impl.MacroSupportBlockCloseStatementImpl;
import ool.idea.macro.psi.impl.MacroSupportBlockOpenStatementImpl;
import ool.idea.macro.psi.impl.MacroSupportBlockStatementImpl;
import ool.idea.macro.psi.impl.MacroSupportDirectiveOpenStatementImpl;
import ool.idea.macro.psi.impl.MacroSupportDirectiveParamFileReferenceImpl;
import ool.idea.macro.psi.impl.MacroSupportDirectiveParamWrapperImpl;
import ool.idea.macro.psi.impl.MacroSupportDirectiveStatementImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroAttributeImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroExpressionParameterImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroParameterImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroTagImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroUnpairedTagImpl;
import ool.idea.macro.psi.impl.MacroSupportMacroXmlPrefixImpl;

/**
 * 12/17/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportPsiElementFactory
{
    public static PsiElement createElement(ASTNode node)
    {
        IElementType type = node.getElementType();
        if (type == MacroSupportTypes.BLOCK_CLOSE_STATEMENT)
        {
            return new MacroSupportBlockCloseStatementImpl(node);
        }
        else if (type == MacroSupportTypes.BLOCK_OPEN_STATEMENT)
        {
            return new MacroSupportBlockOpenStatementImpl(node);
        }
        else if (type == MacroSupportTypes.BLOCK_STATEMENT)
        {
            return new MacroSupportBlockStatementImpl(node);
        }
        else if (type == MacroSupportTypes.DIRECTIVE_OPEN_STATEMENT)
        {
            return new MacroSupportDirectiveOpenStatementImpl(node);
        }
        else if (type == MacroSupportTypes.DIRECTIVE_PARAM_FILE_REFERENCE)
        {
            return new MacroSupportDirectiveParamFileReferenceImpl(node);
        }
        else if (type == MacroSupportTypes.DIRECTIVE_PARAM_WRAPPER)
        {
            return new MacroSupportDirectiveParamWrapperImpl(node);
        }
        else if (type == MacroSupportTypes.DIRECTIVE_STATEMENT)
        {
            return new MacroSupportDirectiveStatementImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_ATTRIBUTE)
        {
            return new MacroSupportMacroAttributeImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_EXPRESSION_PARAMETER)
        {
            return new MacroSupportMacroExpressionParameterImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_PARAMETER)
        {
            return new MacroSupportMacroParameterImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_TAG)
        {
            return new MacroSupportMacroTagImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_UNPAIRED_TAG)
        {
            return new MacroSupportMacroUnpairedTagImpl(node);
        }
        else if (type == MacroSupportTypes.MACRO_XML_PREFIX)
        {
            return new MacroSupportMacroXmlPrefixImpl(node);
        }
        throw new AssertionError("Unknown element type: " + type);
    }
}
