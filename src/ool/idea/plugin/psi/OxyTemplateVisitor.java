package ool.idea.plugin.psi;

import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class OxyTemplateVisitor extends PsiElementVisitor
{
    public void visitBlockCloseStatement(@NotNull BlockCloseStatement o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitBlockOpenStatement(@NotNull BlockOpenStatement o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitBlockStatement(@NotNull BlockStatement o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitDirectiveOpenStatement(@NotNull DirectiveOpenStatement o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitDirectiveParamFileReference(@NotNull DirectiveParamFileReference o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitDirectiveParamWrapper(@NotNull DirectiveParamWrapper o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitDirectiveStatement(@NotNull DirectiveStatement o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroAttribute(@NotNull MacroAttribute o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroExpressionParam(@NotNull MacroExpressionParam o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroName(@NotNull MacroName o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroParam(@NotNull MacroParam o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroParamName(@NotNull MacroParamName o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroTag(@NotNull MacroTag o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroUnpairedTag(@NotNull MacroUnpairedTag o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitMacroXmlPrefix(@NotNull MacroXmlPrefix o)
    {
        visitOxyTemplatePsiElement(o);
    }

    public void visitOxyTemplatePsiElement(@NotNull OxyTemplatePsiElement o)
    {
        visitElement(o);
    }

}
