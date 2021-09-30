// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class OxyTemplateElementVisitor extends PsiElementVisitor {

  public void visitBlockCloseStatement(@NotNull BlockCloseStatement o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitBlockOpenStatement(@NotNull BlockOpenStatement o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitDirectiveOpenStatement(@NotNull DirectiveOpenStatement o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitDirectiveParamFileReference(@NotNull DirectiveParamFileReference o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitDirectiveParamWrapper(@NotNull DirectiveParamWrapper o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitDirectiveStatement(@NotNull DirectiveStatement o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroAttribute(@NotNull MacroAttribute o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroEmptyTag(@NotNull MacroEmptyTag o) {
    visitMacroCall(o);
  }

  public void visitMacroName(@NotNull MacroName o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroParam(@NotNull MacroParam o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroParamName(@NotNull MacroParamName o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroTag(@NotNull MacroTag o) {
    visitMacroCall(o);
  }

  public void visitMacroXmlPrefix(@NotNull MacroXmlPrefix o) {
    visitOxyTemplatePsiElement(o);
  }

  public void visitMacroCall(@NotNull MacroCall o) {
    visitElement(o);
  }

  public void visitOxyTemplatePsiElement(@NotNull OxyTemplatePsiElement o) {
    visitElement(o);
  }

}
