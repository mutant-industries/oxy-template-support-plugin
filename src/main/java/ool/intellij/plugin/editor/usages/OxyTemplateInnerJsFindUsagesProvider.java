package ool.intellij.plugin.editor.usages;

import ool.intellij.plugin.psi.reference.innerjs.globals.GlobalVariableDefinition;

import com.intellij.lang.javascript.findUsages.JavaScriptFindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 7/26/17
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateInnerJsFindUsagesProvider extends JavaScriptFindUsagesProvider
{
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
    {
        return psiElement instanceof GlobalVariableDefinition;
    }

}
