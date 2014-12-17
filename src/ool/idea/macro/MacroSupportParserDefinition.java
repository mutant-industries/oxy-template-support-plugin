package ool.idea.macro;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
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
import ool.idea.macro.file.MacroSupportFile;
import ool.idea.macro.parser.MacroSupportParser;
import ool.idea.macro.psi.MacroSupportPsiElementFactory;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mayrp on 7/23/14.
 */
public class MacroSupportParserDefinition implements ParserDefinition
{
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet HTML = TokenSet.create(MacroSupportTypes.TEMPLATE_HTML_CODE);
    public static final TokenSet COMMENTS = TokenSet.create(MacroSupportTypes.COMMENTS);
//    public static final TokenSet OPEN_BLOCK_MARKERS = TokenSet.create(
//            MacroSupportTypes.BLOCK_OPEN_STATEMENT,
//            MacroSupportTypes.DIRECTIVE_OPEN_STATEMENT,
//            MacroSupportTypes.OPEN_BLOCK_MARKER,
//            MacroSupportTypes.OPEN_BLOCK_MARKER_DIRECTIVE,
//            MacroSupportTypes.OPEN_BLOCK_MARKER_PRINT);
//    public static final TokenSet CLOSE_BLOCK_MARKERS = TokenSet.create(
//            MacroSupportTypes.BLOCK_CLOSE_STATEMENT,
//            MacroSupportTypes.CLOSE_BLOCK_MARKER);

    public static final IFileElementType FILE = new IFileElementType(Language.<MacroSupport>findInstance(MacroSupport.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project)
    {
        return createLexer();
    }

    public Lexer createLexer()
    {
        return new MacroSupportLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens()
    {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens()
    {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements()
    {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project)
    {
        return new MacroSupportParser();
    }

    @Override
    public IFileElementType getFileNodeType()
    {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider)
    {
        return new MacroSupportFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
    {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node)
    {
        return MacroSupportPsiElementFactory.createElement(node);
    }
}
