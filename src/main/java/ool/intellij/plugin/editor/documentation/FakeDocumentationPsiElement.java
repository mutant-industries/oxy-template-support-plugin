package ool.intellij.plugin.editor.documentation;

import ool.intellij.plugin.file.index.OxyTemplateIndexUtil;

import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Nothing but a DTO passing reference to a macro psiElement - used just to pass this element unnoticed by other
 * documentation generators
 *
 * 5/20/16
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
class FakeDocumentationPsiElement extends FakePsiElement
{
    private final JSProperty macro;

    public FakeDocumentationPsiElement(@NotNull JSProperty macro)
    {
        assert OxyTemplateIndexUtil.isMacro(macro);

        this.macro = macro;
    }

    @Override
    public PsiElement getParent()
    {
        return macro;
    }

    @NotNull
    public JSProperty getMacro()
    {
        return macro;
    }

}
