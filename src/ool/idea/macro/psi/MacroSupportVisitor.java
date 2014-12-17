package ool.idea.macro.psi;

import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class MacroSupportVisitor extends PsiElementVisitor
{

    public void visitBlockCloseStatement(@NotNull MacroSupportBlockCloseStatement o)
    {
        visitPsiElement(o);
    }

    public void visitBlockOpenStatement(@NotNull MacroSupportBlockOpenStatement o)
    {
        visitPsiElement(o);
    }

    public void visitBlockStatement(@NotNull MacroSupportBlockStatement o)
    {
        visitPsiElement(o);
    }

    public void visitDirectiveOpenStatement(@NotNull MacroSupportDirectiveOpenStatement o)
    {
        visitPsiElement(o);
    }

    public void visitDirectiveParamFileReference(@NotNull MacroSupportDirectiveParamFileReference o)
    {
        visitPsiElement(o);
    }

    public void visitDirectiveParamWrapper(@NotNull MacroSupportDirectiveParamWrapper o)
    {
        visitPsiElement(o);
    }

    public void visitDirectiveStatement(@NotNull MacroSupportDirectiveStatement o)
    {
        visitPsiElement(o);
    }

    public void visitMacroAttribute(@NotNull MacroSupportMacroAttribute o)
    {
        visitPsiElement(o);
    }

    public void visitMacroExpressionParameter(@NotNull MacroSupportMacroExpressionParameter o)
    {
        visitPsiElement(o);
    }

    public void visitMacroParameter(@NotNull MacroSupportMacroParameter o)
    {
        visitPsiElement(o);
    }

    public void visitMacroTag(@NotNull MacroSupportMacroTag o)
    {
        visitPsiElement(o);
    }

    public void visitMacroUnpairedTag(@NotNull MacroSupportMacroUnpairedTag o)
    {
        visitPsiElement(o);
    }

    public void visitMacroXmlPrefix(@NotNull MacroSupportMacroXmlPrefix o)
    {
        visitPsiElement(o);
    }

    public void visitPsiElement(@NotNull MacroSupportPsiElement o)
    {
        visitElement(o);
    }

}
