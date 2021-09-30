// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ool.intellij.plugin.psi.OxyTemplateTypes.*;
import ool.intellij.plugin.psi.*;
import com.intellij.psi.PsiReference;

public class MacroNameImpl extends OxyTemplatePsiElementImpl implements MacroName {

  public MacroNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitMacroName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public @Nullable PsiReference getReference() {
    return OxyTemplatePsiUtil.getReference(this);
  }

  @Override
  public @NotNull PsiReference[] getReferences() {
    return OxyTemplatePsiUtil.getReferences(this);
  }

  @Override
  public PsiElement setName(@NotNull String newName) {
    return OxyTemplatePsiUtil.setName(this, newName);
  }

  @Override
  public @NotNull String getName() {
    return OxyTemplatePsiUtil.getName(this);
  }

  @Override
  public boolean isClosingTagMacroName() {
    return OxyTemplatePsiUtil.isClosingTagMacroName(this);
  }

}
