// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ool.intellij.plugin.psi.OxyTemplateTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class OxyTemplateParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return oxyTemplate(b, l + 1);
  }

  /* ********************************************************** */
  // T_CLOSE_BLOCK_MARKER
  public static boolean block_close_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_close_statement")) return false;
    if (!nextTokenIs(b, T_CLOSE_BLOCK_MARKER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_CLOSE_BLOCK_MARKER);
    exit_section_(b, m, BLOCK_CLOSE_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // T_OPEN_BLOCK_MARKER | T_OPEN_BLOCK_MARKER_PRINT
  public static boolean block_open_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_open_statement")) return false;
    if (!nextTokenIs(b, "<block open statement>", T_OPEN_BLOCK_MARKER, T_OPEN_BLOCK_MARKER_PRINT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_OPEN_STATEMENT, "<block open statement>");
    r = consumeToken(b, T_OPEN_BLOCK_MARKER);
    if (!r) r = consumeToken(b, T_OPEN_BLOCK_MARKER_PRINT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // block_open_statement T_TEMPLATE_JAVASCRIPT_CODE* block_close_statement
  static boolean block_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement")) return false;
    if (!nextTokenIs(b, "", T_OPEN_BLOCK_MARKER, T_OPEN_BLOCK_MARKER_PRINT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = block_open_statement(b, l + 1);
    r = r && block_statement_1(b, l + 1);
    p = r; // pin = 2
    r = r && block_close_statement(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // T_TEMPLATE_JAVASCRIPT_CODE*
  private static boolean block_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, T_TEMPLATE_JAVASCRIPT_CODE)) break;
      if (!empty_element_parsed_guard_(b, "block_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // T_OPEN_BLOCK_MARKER_DIRECTIVE
  public static boolean directive_open_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "directive_open_statement")) return false;
    if (!nextTokenIs(b, T_OPEN_BLOCK_MARKER_DIRECTIVE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_OPEN_BLOCK_MARKER_DIRECTIVE);
    exit_section_(b, m, DIRECTIVE_OPEN_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // T_DIRECTIVE_PARAM
  public static boolean directive_param_file_reference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "directive_param_file_reference")) return false;
    if (!nextTokenIs(b, T_DIRECTIVE_PARAM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_DIRECTIVE_PARAM);
    exit_section_(b, m, DIRECTIVE_PARAM_FILE_REFERENCE, r);
    return r;
  }

  /* ********************************************************** */
  // T_DIRECTIVE_PARAM_BOUNDARY directive_param_file_reference T_DIRECTIVE_PARAM_BOUNDARY
  public static boolean directive_param_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "directive_param_wrapper")) return false;
    if (!nextTokenIs(b, T_DIRECTIVE_PARAM_BOUNDARY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DIRECTIVE_PARAM_WRAPPER, null);
    r = consumeToken(b, T_DIRECTIVE_PARAM_BOUNDARY);
    p = r; // pin = 1
    r = r && report_error_(b, directive_param_file_reference(b, l + 1));
    r = p && consumeToken(b, T_DIRECTIVE_PARAM_BOUNDARY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // directive_open_statement T_DIRECTIVE directive_param_wrapper+ block_close_statement
  public static boolean directive_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "directive_statement")) return false;
    if (!nextTokenIs(b, T_OPEN_BLOCK_MARKER_DIRECTIVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DIRECTIVE_STATEMENT, null);
    r = directive_open_statement(b, l + 1);
    r = r && consumeToken(b, T_DIRECTIVE);
    p = r; // pin = 2
    r = r && report_error_(b, directive_statement_2(b, l + 1));
    r = p && block_close_statement(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // directive_param_wrapper+
  private static boolean directive_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "directive_statement_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = directive_param_wrapper(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!directive_param_wrapper(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "directive_statement_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // block_statement|block_open_statement|block_close_statement|
  //     directive_statement|directive_param_wrapper|directive_param_file_reference|directive_open_statement|
  //     macro_tag|macro_empty_tag|macro_xml_prefix|macro_attribute|macro_name|
  //     macro_param|macro_param_name|macro_expression_param|
  //     T_TEMPLATE_HTML_CODE|T_TEMPLATE_JAVASCRIPT_CODE|T_INNER_TEMPLATE_ELEMENT|T_OUTER_TEMPLATE_ELEMENT|T_BLOCK_COMMENT
  //     T_OPEN_BLOCK_MARKER|T_OPEN_BLOCK_MARKER_PRINT|T_OPEN_BLOCK_MARKER_DIRECTIVE|T_CLOSE_BLOCK_MARKER|
  //     T_DIRECTIVE|T_DIRECTIVE_PARAM|T_DIRECTIVE_PARAM_BOUNDARY|
  //     T_XML_TAG_START|T_XML_OPEN_TAG_END|T_XML_CLOSE_TAG_START|T_XML_CLOSE_TAG_END|T_XML_EMPTY_TAG_END|T_XML_NAMESPACE_DELIMITER|
  //     T_MACRO_XML_NAMESPACE|T_MACRO_NAME|T_XML_ENCODED_ENTITY|
  //     T_MACRO_PARAM_NAME|T_MACRO_PARAM_ASSIGNMENT|T_MACRO_PARAM_BOUNDARY|T_MACRO_PARAM|T_MACRO_PARAM_EXPRESSION_STATEMENT
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = block_statement(b, l + 1);
    if (!r) r = block_open_statement(b, l + 1);
    if (!r) r = block_close_statement(b, l + 1);
    if (!r) r = directive_statement(b, l + 1);
    if (!r) r = directive_param_wrapper(b, l + 1);
    if (!r) r = directive_param_file_reference(b, l + 1);
    if (!r) r = directive_open_statement(b, l + 1);
    if (!r) r = macro_tag(b, l + 1);
    if (!r) r = macro_empty_tag(b, l + 1);
    if (!r) r = macro_xml_prefix(b, l + 1);
    if (!r) r = macro_attribute(b, l + 1);
    if (!r) r = macro_name(b, l + 1);
    if (!r) r = macro_param(b, l + 1);
    if (!r) r = macro_param_name(b, l + 1);
    if (!r) r = macro_expression_param(b, l + 1);
    if (!r) r = consumeToken(b, T_TEMPLATE_HTML_CODE);
    if (!r) r = consumeToken(b, T_TEMPLATE_JAVASCRIPT_CODE);
    if (!r) r = consumeToken(b, T_INNER_TEMPLATE_ELEMENT);
    if (!r) r = consumeToken(b, T_OUTER_TEMPLATE_ELEMENT);
    if (!r) r = parseTokens(b, 0, T_BLOCK_COMMENT, T_OPEN_BLOCK_MARKER);
    if (!r) r = consumeToken(b, T_OPEN_BLOCK_MARKER_PRINT);
    if (!r) r = consumeToken(b, T_OPEN_BLOCK_MARKER_DIRECTIVE);
    if (!r) r = consumeToken(b, T_CLOSE_BLOCK_MARKER);
    if (!r) r = consumeToken(b, T_DIRECTIVE);
    if (!r) r = consumeToken(b, T_DIRECTIVE_PARAM);
    if (!r) r = consumeToken(b, T_DIRECTIVE_PARAM_BOUNDARY);
    if (!r) r = consumeToken(b, T_XML_TAG_START);
    if (!r) r = consumeToken(b, T_XML_OPEN_TAG_END);
    if (!r) r = consumeToken(b, T_XML_CLOSE_TAG_START);
    if (!r) r = consumeToken(b, T_XML_CLOSE_TAG_END);
    if (!r) r = consumeToken(b, T_XML_EMPTY_TAG_END);
    if (!r) r = consumeToken(b, T_XML_NAMESPACE_DELIMITER);
    if (!r) r = consumeToken(b, T_MACRO_XML_NAMESPACE);
    if (!r) r = consumeToken(b, T_MACRO_NAME);
    if (!r) r = consumeToken(b, T_XML_ENCODED_ENTITY);
    if (!r) r = consumeToken(b, T_MACRO_PARAM_NAME);
    if (!r) r = consumeToken(b, T_MACRO_PARAM_ASSIGNMENT);
    if (!r) r = consumeToken(b, T_MACRO_PARAM_BOUNDARY);
    if (!r) r = consumeToken(b, T_MACRO_PARAM);
    if (!r) r = consumeToken(b, T_MACRO_PARAM_EXPRESSION_STATEMENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // macro_param_name T_MACRO_PARAM_ASSIGNMENT T_MACRO_PARAM_BOUNDARY macro_param? T_MACRO_PARAM_BOUNDARY
  public static boolean macro_attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_attribute")) return false;
    if (!nextTokenIs(b, T_MACRO_PARAM_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MACRO_ATTRIBUTE, null);
    r = macro_param_name(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeTokens(b, -1, T_MACRO_PARAM_ASSIGNMENT, T_MACRO_PARAM_BOUNDARY));
    r = p && report_error_(b, macro_attribute_3(b, l + 1)) && r;
    r = p && consumeToken(b, T_MACRO_PARAM_BOUNDARY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // macro_param?
  private static boolean macro_attribute_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_attribute_3")) return false;
    macro_param(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // T_XML_TAG_START macro_xml_prefix macro_name macro_attribute* T_XML_EMPTY_TAG_END
  public static boolean macro_empty_tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_empty_tag")) return false;
    if (!nextTokenIs(b, T_XML_TAG_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MACRO_EMPTY_TAG, null);
    r = consumeToken(b, T_XML_TAG_START);
    r = r && macro_xml_prefix(b, l + 1);
    r = r && macro_name(b, l + 1);
    p = r; // pin = 3
    r = r && report_error_(b, macro_empty_tag_3(b, l + 1));
    r = p && consumeToken(b, T_XML_EMPTY_TAG_END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // macro_attribute*
  private static boolean macro_empty_tag_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_empty_tag_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!macro_attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_empty_tag_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // T_MACRO_PARAM_EXPRESSION_STATEMENT T_TEMPLATE_JAVASCRIPT_CODE
  static boolean macro_expression_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_expression_param")) return false;
    if (!nextTokenIs(b, T_MACRO_PARAM_EXPRESSION_STATEMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, T_MACRO_PARAM_EXPRESSION_STATEMENT, T_TEMPLATE_JAVASCRIPT_CODE);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // T_MACRO_NAME
  public static boolean macro_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_name")) return false;
    if (!nextTokenIs(b, T_MACRO_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_MACRO_NAME);
    exit_section_(b, m, MACRO_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // (T_MACRO_PARAM | T_XML_ENCODED_ENTITY)+ | T_TEMPLATE_JAVASCRIPT_CODE | macro_expression_param
  public static boolean macro_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_PARAM, "<macro param>");
    r = macro_param_0(b, l + 1);
    if (!r) r = consumeToken(b, T_TEMPLATE_JAVASCRIPT_CODE);
    if (!r) r = macro_expression_param(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (T_MACRO_PARAM | T_XML_ENCODED_ENTITY)+
  private static boolean macro_param_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_param_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = macro_param_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!macro_param_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_param_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // T_MACRO_PARAM | T_XML_ENCODED_ENTITY
  private static boolean macro_param_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_param_0_0")) return false;
    boolean r;
    r = consumeToken(b, T_MACRO_PARAM);
    if (!r) r = consumeToken(b, T_XML_ENCODED_ENTITY);
    return r;
  }

  /* ********************************************************** */
  // T_MACRO_PARAM_NAME
  public static boolean macro_param_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_param_name")) return false;
    if (!nextTokenIs(b, T_MACRO_PARAM_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_MACRO_PARAM_NAME);
    exit_section_(b, m, MACRO_PARAM_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // T_XML_TAG_START macro_xml_prefix macro_name macro_attribute* T_XML_OPEN_TAG_END
  //     (macro_tag | macro_empty_tag | block_statement | directive_statement | T_TEMPLATE_HTML_CODE)* T_XML_CLOSE_TAG_START macro_xml_prefix macro_name T_XML_CLOSE_TAG_END
  public static boolean macro_tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_tag")) return false;
    if (!nextTokenIs(b, T_XML_TAG_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MACRO_TAG, null);
    r = consumeToken(b, T_XML_TAG_START);
    r = r && macro_xml_prefix(b, l + 1);
    r = r && macro_name(b, l + 1);
    r = r && macro_tag_3(b, l + 1);
    r = r && consumeToken(b, T_XML_OPEN_TAG_END);
    p = r; // pin = 5
    r = r && report_error_(b, macro_tag_5(b, l + 1));
    r = p && report_error_(b, consumeToken(b, T_XML_CLOSE_TAG_START)) && r;
    r = p && report_error_(b, macro_xml_prefix(b, l + 1)) && r;
    r = p && report_error_(b, macro_name(b, l + 1)) && r;
    r = p && consumeToken(b, T_XML_CLOSE_TAG_END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // macro_attribute*
  private static boolean macro_tag_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_tag_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!macro_attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_tag_3", c)) break;
    }
    return true;
  }

  // (macro_tag | macro_empty_tag | block_statement | directive_statement | T_TEMPLATE_HTML_CODE)*
  private static boolean macro_tag_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_tag_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!macro_tag_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_tag_5", c)) break;
    }
    return true;
  }

  // macro_tag | macro_empty_tag | block_statement | directive_statement | T_TEMPLATE_HTML_CODE
  private static boolean macro_tag_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_tag_5_0")) return false;
    boolean r;
    r = macro_tag(b, l + 1);
    if (!r) r = macro_empty_tag(b, l + 1);
    if (!r) r = block_statement(b, l + 1);
    if (!r) r = directive_statement(b, l + 1);
    if (!r) r = consumeToken(b, T_TEMPLATE_HTML_CODE);
    return r;
  }

  /* ********************************************************** */
  // T_MACRO_XML_NAMESPACE T_XML_NAMESPACE_DELIMITER
  public static boolean macro_xml_prefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_xml_prefix")) return false;
    if (!nextTokenIs(b, T_MACRO_XML_NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, T_MACRO_XML_NAMESPACE, T_XML_NAMESPACE_DELIMITER);
    exit_section_(b, m, MACRO_XML_PREFIX, r);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean oxyTemplate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "oxyTemplate")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "oxyTemplate", c)) break;
    }
    return true;
  }

}
