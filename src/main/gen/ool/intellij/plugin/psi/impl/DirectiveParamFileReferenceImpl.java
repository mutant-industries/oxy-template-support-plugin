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

public class DirectiveParamFileReferenceImpl extends OxyTemplatePsiElementImpl implements DirectiveParamFileReference {

  public DirectiveParamFileReferenceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitDirectiveParamFileReference(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public @NotNull PsiReference[] getReferences() {
    return OxyTemplatePsiUtil.getReferences(this);
  }

}
