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

public class DirectiveStatementImpl extends OxyTemplatePsiElementImpl implements DirectiveStatement {

  public DirectiveStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitDirectiveStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BlockCloseStatement getBlockCloseStatement() {
    return findChildByClass(BlockCloseStatement.class);
  }

  @Override
  @NotNull
  public DirectiveOpenStatement getDirectiveOpenStatement() {
    return findNotNullChildByClass(DirectiveOpenStatement.class);
  }

  @Override
  @NotNull
  public List<DirectiveParamWrapper> getDirectiveParamWrapperList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DirectiveParamWrapper.class);
  }

  @Override
  public String getName() {
    return OxyTemplatePsiUtil.getName(this);
  }

}
