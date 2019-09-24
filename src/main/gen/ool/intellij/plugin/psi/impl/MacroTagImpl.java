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
import com.intellij.openapi.util.TextRange;

public class MacroTagImpl extends MacroCallImpl implements MacroTag {

  public MacroTagImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitMacroTag(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BlockCloseStatement> getBlockCloseStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BlockCloseStatement.class);
  }

  @Override
  @NotNull
  public List<BlockOpenStatement> getBlockOpenStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BlockOpenStatement.class);
  }

  @Override
  @NotNull
  public List<DirectiveStatement> getDirectiveStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, DirectiveStatement.class);
  }

  @Override
  @NotNull
  public List<MacroAttribute> getMacroAttributeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroAttribute.class);
  }

  @Override
  @NotNull
  public List<MacroEmptyTag> getMacroEmptyTagList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroEmptyTag.class);
  }

  @Override
  @NotNull
  public List<MacroName> getMacroNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroName.class);
  }

  @Override
  @NotNull
  public List<MacroTag> getMacroTagList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroTag.class);
  }

  @Override
  @NotNull
  public List<MacroXmlPrefix> getMacroXmlPrefixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroXmlPrefix.class);
  }

  @Override
  @NotNull
  public MacroName getMacroName() {
    List<MacroName> p1 = getMacroNameList();
    return p1.get(0);
  }

  @Override
  @NotNull
  public TextRange getContentRange() {
    return OxyTemplatePsiUtil.getContentRange(this);
  }

}
