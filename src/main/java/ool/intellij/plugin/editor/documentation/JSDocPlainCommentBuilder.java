package ool.intellij.plugin.editor.documentation;

import java.util.List;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;
import ool.intellij.plugin.lang.I18nSupport;

import com.intellij.lang.javascript.documentation.JSDocumentationBuilder;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 5/20/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
class JSDocPlainCommentBuilder extends JSDocumentationBuilder
{
    private final StringBuilder docBuilder;
    private final StringBuilder seeAlsoBuilder;
    private final PsiElement comment;
    private final OxyTemplateDocumentationProvider provider;

    public JSDocPlainCommentBuilder(@NotNull PsiElement element, PsiElement contextElement, @Nullable PsiComment comment, OxyTemplateDocumentationProvider provider)
    {
        super(element, contextElement, comment, provider);

        this.comment = element;
        this.provider = provider;

        this.docBuilder = new StringBuilder();
        this.seeAlsoBuilder = new StringBuilder();
    }

    @Override
    public boolean onCommentLine(@NotNull String line)
    {
        docBuilder.append(line);

        return true;
    }

    @Override
    public boolean onPatternMatch(@NotNull MetaDocType metaDocType, @Nullable String matchName, @Nullable String matchValue,
                                  @Nullable String remainingLineContent, @NotNull String commentText, @NotNull String matched)
    {
        if (metaDocType == MetaDocType.SEE && remainingLineContent != null)
        {
            List<JSElement> references = OxyTemplateIndexUtil.getJsMacroNameReferences(remainingLineContent, comment.getProject());

            if (references.size() != 0 && references.get(0) instanceof JSProperty)
            {
                JSProperty element = (JSProperty) references.get(0);

                seeAlsoBuilder.append("<br/>").append(I18nSupport.message("macro.param.see.also.heading"))
                        .append(provider.generateJsMacroDocumentation(element, element));
            }
        }

        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @NotNull
    public String getDoc()
    {
        return docBuilder.toString();
    }

    @NotNull
    public String getSeeAlso()
    {
        return seeAlsoBuilder.toString();
    }

}
