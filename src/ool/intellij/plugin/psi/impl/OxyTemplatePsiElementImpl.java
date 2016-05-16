package ool.intellij.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplatePsiElementImpl extends ASTWrapperPsiElement
{
    public OxyTemplatePsiElementImpl(@NotNull ASTNode node)
    {
        super(node);
    }

    @Override
    public String toString()
    {
        return getNode().getElementType().toString();
    }

}
