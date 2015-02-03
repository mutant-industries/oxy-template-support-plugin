package ool.idea.plugin.editor.folding;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import java.util.LinkedList;
import java.util.List;
import ool.idea.plugin.file.OxyTemplateFileType;
import ool.idea.plugin.lang.parser.OxyTemplateParserDefinition;
import ool.idea.plugin.editor.highlighter.OxyTemplateSyntaxHighlighter;
import ool.idea.plugin.psi.OxyTemplateTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 1/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MatchingTagsAndJsBlock extends CustomFoldingBuilder
{
    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root,
                                            @NotNull Document document, boolean quick)
    {
        PsiFile file = root.getContainingFile();
        FileType fileType = file.getFileType();

        if ( ! (fileType instanceof OxyTemplateFileType))
        {
            return;
        }

        buildBraceMatcherBasedFolding(descriptors, root, document, new OxyTemplateSyntaxHighlighter());
    }

    private static void buildBraceMatcherBasedFolding(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root,
                                                      @NotNull Document document, @NotNull SyntaxHighlighter highlighter)
    {
        LexerEditorHighlighter editorHighlighter = new LexerEditorHighlighter(highlighter, EditorColorsManager.getInstance().getGlobalScheme());
        editorHighlighter.setText(document.getText());
        FileType fileType = root.getContainingFile().getFileType();
        BraceMatcher braceMatcher = BraceMatchingUtil.getBraceMatcher(fileType, root.getLanguage());
        TextRange totalRange = root.getTextRange();
        final HighlighterIterator iterator = editorHighlighter.createIterator(totalRange.getStartOffset());

        final LinkedList<Trinity<Integer, Integer, IElementType>> stack = new LinkedList<Trinity<Integer, Integer, IElementType>>();
        String editorText = document.getText();
        TextRange range;

        while ( ! iterator.atEnd() && iterator.getStart() < totalRange.getEndOffset())
        {
            final Trinity<Integer, Integer, IElementType> last;

            if(OxyTemplateParserDefinition.PARAMETER_QUOTES.contains(iterator.getTokenType()))
            {
                iterator.advance();
                continue;
            }

            if (braceMatcher.isLBraceToken(iterator, editorText, fileType))
            {
                if(iterator.getTokenType() == OxyTemplateTypes.T_XML_TAG_START)
                {
                    IElementType tokenType = iterator.getTokenType();

                    do
                    {
                        iterator.advance();
                    }
                    while( ! iterator.atEnd() && iterator.getTokenType() != OxyTemplateTypes.T_XML_OPEN_TAG_END
                            && iterator.getTokenType() != OxyTemplateTypes.T_XML_CLOSE_TAG_END
                            && iterator.getTokenType() != OxyTemplateTypes.T_XML_UNPAIRED_TAG_END);

                    if(iterator.atEnd())
                    {
                        return;
                    }

                    stack.addLast(Trinity.create(iterator.getStart(), iterator.getEnd(), tokenType));
                    iterator.retreat();
                }
                else
                {
                    stack.addLast(Trinity.create(iterator.getStart() + 2, iterator.getEnd(), iterator.getTokenType()));
                }
            }
            else if (braceMatcher.isRBraceToken(iterator, editorText, fileType)
                    && !stack.isEmpty() && braceMatcher.isPairBraces((last = stack.getLast()).third, iterator.getTokenType()))
            {
                stack.removeLast();

                range = new TextRange(last.first, iterator.getEnd() -
                        (iterator.getTokenType() == OxyTemplateTypes.T_XML_CLOSE_TAG_END ? 1 : 2));

                if (last.third != OxyTemplateTypes.T_XML_UNPAIRED_TAG_END
                    && StringUtil.countChars(document.getText(range), '\n') >= 2)
                {
                    descriptors.add(new FoldingDescriptor(root, range));
                }
            }

            iterator.advance();
        }
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range)
    {
        return "...";
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node)
    {
        return false;
    }

}
