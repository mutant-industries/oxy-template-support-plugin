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

public class MacroEmptyTagImpl extends MacroCallImpl implements MacroEmptyTag {

  public MacroEmptyTagImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull OxyTemplateElementVisitor visitor) {
    visitor.visitMacroEmptyTag(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OxyTemplateElementVisitor) accept((OxyTemplateElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<MacroAttribute> getMacroAttributeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MacroAttribute.class);
  }

  @Override
  @NotNull
  public MacroName getMacroName() {
    return findNotNullChildByClass(MacroName.class);
  }

  @Override
  @NotNull
  public MacroXmlPrefix getMacroXmlPrefix() {
    return findNotNullChildByClass(MacroXmlPrefix.class);
  }

}
