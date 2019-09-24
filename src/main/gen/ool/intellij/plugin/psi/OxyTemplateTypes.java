// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ool.intellij.plugin.psi.impl.*;

public interface OxyTemplateTypes {

  IElementType BLOCK_CLOSE_STATEMENT = new OxyTemplateElementType("BLOCK_CLOSE_STATEMENT");
  IElementType BLOCK_OPEN_STATEMENT = new OxyTemplateElementType("BLOCK_OPEN_STATEMENT");
  IElementType DIRECTIVE_OPEN_STATEMENT = new OxyTemplateElementType("DIRECTIVE_OPEN_STATEMENT");
  IElementType DIRECTIVE_PARAM_FILE_REFERENCE = new OxyTemplateElementType("DIRECTIVE_PARAM_FILE_REFERENCE");
  IElementType DIRECTIVE_PARAM_WRAPPER = new OxyTemplateElementType("DIRECTIVE_PARAM_WRAPPER");
  IElementType DIRECTIVE_STATEMENT = new OxyTemplateElementType("DIRECTIVE_STATEMENT");
  IElementType MACRO_ATTRIBUTE = new OxyTemplateElementType("MACRO_ATTRIBUTE");
  IElementType MACRO_EMPTY_TAG = new OxyTemplateElementType("MACRO_EMPTY_TAG");
  IElementType MACRO_NAME = new OxyTemplateElementType("MACRO_NAME");
  IElementType MACRO_PARAM = new OxyTemplateElementType("MACRO_PARAM");
  IElementType MACRO_PARAM_NAME = new OxyTemplateElementType("MACRO_PARAM_NAME");
  IElementType MACRO_TAG = new OxyTemplateElementType("MACRO_TAG");
  IElementType MACRO_XML_PREFIX = new OxyTemplateElementType("MACRO_XML_PREFIX");

  IElementType T_BLOCK_COMMENT = new OxyTemplateTokenType("T_BLOCK_COMMENT");
  IElementType T_CLOSE_BLOCK_MARKER = new OxyTemplateTokenType("T_CLOSE_BLOCK_MARKER");
  IElementType T_DIRECTIVE = new OxyTemplateTokenType("T_DIRECTIVE");
  IElementType T_DIRECTIVE_PARAM = new OxyTemplateTokenType("T_DIRECTIVE_PARAM");
  IElementType T_DIRECTIVE_PARAM_BOUNDARY = new OxyTemplateTokenType("T_DIRECTIVE_PARAM_BOUNDARY");
  IElementType T_INNER_TEMPLATE_ELEMENT = new OxyTemplateTokenType("T_INNER_TEMPLATE_ELEMENT");
  IElementType T_MACRO_NAME = new OxyTemplateTokenType("T_MACRO_NAME");
  IElementType T_MACRO_PARAM = new OxyTemplateTokenType("T_MACRO_PARAM");
  IElementType T_MACRO_PARAM_ASSIGNMENT = new OxyTemplateTokenType("T_MACRO_PARAM_ASSIGNMENT");
  IElementType T_MACRO_PARAM_BOUNDARY = new OxyTemplateTokenType("T_MACRO_PARAM_BOUNDARY");
  IElementType T_MACRO_PARAM_EXPRESSION_STATEMENT = new OxyTemplateTokenType("T_MACRO_PARAM_EXPRESSION_STATEMENT");
  IElementType T_MACRO_PARAM_NAME = new OxyTemplateTokenType("T_MACRO_PARAM_NAME");
  IElementType T_MACRO_XML_NAMESPACE = new OxyTemplateTokenType("T_MACRO_XML_NAMESPACE");
  IElementType T_OPEN_BLOCK_MARKER = new OxyTemplateTokenType("T_OPEN_BLOCK_MARKER");
  IElementType T_OPEN_BLOCK_MARKER_DIRECTIVE = new OxyTemplateTokenType("T_OPEN_BLOCK_MARKER_DIRECTIVE");
  IElementType T_OPEN_BLOCK_MARKER_PRINT = new OxyTemplateTokenType("T_OPEN_BLOCK_MARKER_PRINT");
  IElementType T_OUTER_TEMPLATE_ELEMENT = new OxyTemplateTokenType("T_OUTER_TEMPLATE_ELEMENT");
  IElementType T_TEMPLATE_HTML_CODE = new OxyTemplateTokenType("T_TEMPLATE_HTML_CODE");
  IElementType T_TEMPLATE_JAVASCRIPT_CODE = new OxyTemplateTokenType("T_TEMPLATE_JAVASCRIPT_CODE");
  IElementType T_XML_CLOSE_TAG_END = new OxyTemplateTokenType("T_XML_CLOSE_TAG_END");
  IElementType T_XML_CLOSE_TAG_START = new OxyTemplateTokenType("T_XML_CLOSE_TAG_START");
  IElementType T_XML_EMPTY_TAG_END = new OxyTemplateTokenType("T_XML_EMPTY_TAG_END");
  IElementType T_XML_ENCODED_ENTITY = new OxyTemplateTokenType("T_XML_ENCODED_ENTITY");
  IElementType T_XML_NAMESPACE_DELIMITER = new OxyTemplateTokenType("T_XML_NAMESPACE_DELIMITER");
  IElementType T_XML_OPEN_TAG_END = new OxyTemplateTokenType("T_XML_OPEN_TAG_END");
  IElementType T_XML_TAG_START = new OxyTemplateTokenType("T_XML_TAG_START");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BLOCK_CLOSE_STATEMENT) {
        return new BlockCloseStatementImpl(node);
      }
      else if (type == BLOCK_OPEN_STATEMENT) {
        return new BlockOpenStatementImpl(node);
      }
      else if (type == DIRECTIVE_OPEN_STATEMENT) {
        return new DirectiveOpenStatementImpl(node);
      }
      else if (type == DIRECTIVE_PARAM_FILE_REFERENCE) {
        return new DirectiveParamFileReferenceImpl(node);
      }
      else if (type == DIRECTIVE_PARAM_WRAPPER) {
        return new DirectiveParamWrapperImpl(node);
      }
      else if (type == DIRECTIVE_STATEMENT) {
        return new DirectiveStatementImpl(node);
      }
      else if (type == MACRO_ATTRIBUTE) {
        return new MacroAttributeImpl(node);
      }
      else if (type == MACRO_EMPTY_TAG) {
        return new MacroEmptyTagImpl(node);
      }
      else if (type == MACRO_NAME) {
        return new MacroNameImpl(node);
      }
      else if (type == MACRO_PARAM) {
        return new MacroParamImpl(node);
      }
      else if (type == MACRO_PARAM_NAME) {
        return new MacroParamNameImpl(node);
      }
      else if (type == MACRO_TAG) {
        return new MacroTagImpl(node);
      }
      else if (type == MACRO_XML_PREFIX) {
        return new MacroXmlPrefixImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
