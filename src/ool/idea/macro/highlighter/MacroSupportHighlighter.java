package ool.idea.macro.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import ool.idea.macro.MacroSupportParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/25/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportHighlighter extends SyntaxHighlighterBase
{
    private final Project myProject;

    public MacroSupportHighlighter(@Nullable Project project)
    {
        this.myProject = project;
    }

    @NotNull
    public Lexer getHighlightingLexer()
    {
        return new MacroSupportParserDefinition().createLexer(this.myProject);
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
    {
        return EMPTY;
    }
}
