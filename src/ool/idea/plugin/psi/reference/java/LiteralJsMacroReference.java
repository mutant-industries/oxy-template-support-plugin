package ool.idea.plugin.psi.reference.java;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import ool.idea.plugin.file.index.nacros.js.JsMacroNameIndex;
import ool.idea.plugin.psi.reference.MacroReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class LiteralJsMacroReference  extends MacroReference<PsiLiteralExpression>
{
    protected final JSElement reference;

    public LiteralJsMacroReference(@NotNull PsiLiteralExpression literalExpression, @Nullable JSElement reference)
    {
        super(literalExpression);
        this.reference = reference;
    }

    @Nullable
    @Override
    public PsiElement resolve()
    {
        return reference;
    }

    @Override
    public TextRange getRangeInElement()
    {
        return TextRange.create(1, referencedIdentifier.getTextLength() - 1);
    }

    @Override
    public boolean isReferenceTo(PsiElement element)
    {
        return element.isEquivalentTo(reference);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
    {
        String originalText = referencedIdentifier.getText();
        String newLiteralValue = originalText.replaceAll("\\.[^\\.]+?$", ".") + newElementName + "\"";

        return referencedIdentifier.replace(JavaPsiFacade.getInstance(referencedIdentifier.getProject())
                .getElementFactory().createExpressionFromText(newLiteralValue, null));
    }

    @NotNull
    @Override
    public Object[] getVariants()
    {
        return FileBasedIndex.getInstance().getAllKeys(JsMacroNameIndex.INDEX_ID, referencedIdentifier.getProject()).toArray();
    }

}
