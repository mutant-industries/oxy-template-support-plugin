package ool.intellij.plugin.psi.reference;

import java.util.LinkedList;
import java.util.List;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;

/**
 * 1/24/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroReferenceSet
{
    private final PsiElement element;

    public MacroReferenceSet(PsiElement element)
    {
        this.element = element;
    }

    public PsiReference[] getAllReferences()
    {
        String text = element.getText().replaceAll("(^\")|(\"$)", "");
        int offsetShift = element instanceof PsiLiteralExpression ? 1 : 0;  // string literal quote demarcation
        List<PsiReference> references = new LinkedList<>();

        references.add(new MacroReference(element, text.lastIndexOf(".") + 1 + offsetShift, text.length() + offsetShift));

        while (text.contains("."))
        {
            text = text.substring(0, text.lastIndexOf("."));
            references.add(new MacroReference(element, text.lastIndexOf(".") + 1 + offsetShift, text.length() + offsetShift));
        }

        return references.toArray(new PsiReference[references.size()]);
    }

}
