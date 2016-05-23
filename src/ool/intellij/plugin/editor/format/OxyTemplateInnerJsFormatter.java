package ool.intellij.plugin.editor.format;

import ool.intellij.plugin.editor.format.block.innerJs.InnerJsBlock;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.formatter.JSCodeStyleSettings;
import com.intellij.lang.javascript.formatter.JavascriptFormattingModelBuilder;
import com.intellij.lang.javascript.formatter.blocks.JSBlock;
import com.intellij.lang.javascript.formatter.blocks.alignment.ASTNodeBasedAlignmentFactory;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/25/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsFormatter extends JavascriptFormattingModelBuilder
{
    @Override
    public JSBlock createSubBlock(@NotNull ASTNode child, Alignment childAlignment, Indent childIndent, Wrap wrap,
                                  @NotNull CodeStyleSettings topSettings, @NotNull Language dialect, @Nullable ASTNodeBasedAlignmentFactory sharedAlignmentFactory, @NotNull JSCodeStyleSettings jsCodeStyleSettings)
    {
        return new InnerJsBlock(child, childAlignment, childIndent, wrap, topSettings, sharedAlignmentFactory, dialect, jsCodeStyleSettings);
    }
}
