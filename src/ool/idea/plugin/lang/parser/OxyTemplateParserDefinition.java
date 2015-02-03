package ool.idea.plugin.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlTokenType;
import ool.idea.plugin.file.OxyTemplateFile;
import ool.idea.plugin.lang.OxyTemplate;
import ool.idea.plugin.lang.lexer.OxyTemplateLexerAdapter;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateParserDefinition implements ParserDefinition
{
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet HTML = TokenSet.create(
            OxyTemplateTypes.T_TEMPLATE_HTML_CODE,
            OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT
    );
    public static final TokenSet INNER_JS = TokenSet.create(
            OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE,
            OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT
    );
    public static final TokenSet OPEN_BLOCK_MARKERS = TokenSet.create(
            OxyTemplateTypes.BLOCK_OPEN_STATEMENT,
            OxyTemplateTypes.DIRECTIVE_OPEN_STATEMENT,
            OxyTemplateTypes.T_OPEN_BLOCK_MARKER,
            OxyTemplateTypes.T_OPEN_BLOCK_MARKER_DIRECTIVE,
            OxyTemplateTypes.T_OPEN_BLOCK_MARKER_PRINT);
    public static final TokenSet CLOSE_BLOCK_MARKERS = TokenSet.create(
            OxyTemplateTypes.BLOCK_CLOSE_STATEMENT,
            OxyTemplateTypes.T_CLOSE_BLOCK_MARKER);
    public static final TokenSet PARAMETER_QUOTES = TokenSet.create(
            OxyTemplateTypes.T_DIRECTIVE_PARAM_BOUNDARY,
            OxyTemplateTypes.T_MACRO_PARAM_BOUNDARY,
            XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER,
            XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER);

    public static final IFileElementType FILE = new IFileElementType(OxyTemplate.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project)
    {
        return createLexer();
    }

    public Lexer createLexer()
    {
        return new OxyTemplateLexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens()
    {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens()
    {
        return TokenSet.create(OxyTemplateTypes.T_BLOCK_COMMENT);
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements()
    {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project)
    {
        return new OxyTemplateParser();
    }

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider)
    {
        return new OxyTemplateFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
    {
        return SpaceRequirements.MAY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node)
    {
        return OxyTemplateTypes.Factory.createElement(node);
    }

}
