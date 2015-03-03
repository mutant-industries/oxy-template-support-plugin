package ool.idea.plugin.lang;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.templateLanguages.SimpleTreePatcher;

/**
 * 3/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsTreePatcher extends SimpleTreePatcher
{
    @Override
    public void insert(CompositeElement parent, TreeElement anchorBefore, OuterLanguageElement toInsert)
    {
        int parentStartOffset = parent.getStartOffset();

        /**
         * just avoid this <% oxy. %><% iftrue(); %> (anchorBefore.getStartOffset() != parentStartOffset),
         *  which will result in the following:
         *
         *  PsiElement(Inner Template Element)('<% ')(0,3)
         *  JSExpressionStatement(3,22)
         *      JSCallExpression(3,21)
         *          JSReferenceExpression(3,19)
         *              JSReferenceExpression(3,6)
         *                  PsiElement(JS:IDENTIFIER)('oxy')(3,6)
         *              PsiElement(JS:DOT)('.')(6,7)
         *              PsiElement(Inner Template Element)(' %><% ')(7,13)      <-
         *              PsiElement(JS:IDENTIFIER)('ifTrue')(13,19)
         *          JSArgumentList(19,21)
         *              PsiElement(JS:LPAR)('(')(19,20)
         *              PsiElement(JS:RPAR)(')')(20,21)
         *      PsiElement(JS:SEMICOLON)(';')(21,22)
         *  PsiElement(Inner Template Element)(' %>')(22,25)
         */
        if(anchorBefore != null && parent.getTreeParent() != null && anchorBefore.getStartOffset() == parentStartOffset
                && parent.getTreeParent().getStartOffset() == parentStartOffset)
        {
            while(parent.getTreeParent() != null &&  ! (parent.getTreeParent() instanceof FileElement)
                    && parent.getTreeParent().getStartOffset() == parentStartOffset)
            {
                parent = parent.getTreeParent();
            }

            parent.rawInsertBeforeMe((TreeElement)toInsert);
        }
        else
        {
            super.insert(parent, anchorBefore, toInsert);
        }
    }

}
