// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface DirectiveStatement extends OxyTemplatePsiElement {

  @Nullable
  BlockCloseStatement getBlockCloseStatement();

  @NotNull
  DirectiveOpenStatement getDirectiveOpenStatement();

  @NotNull
  List<DirectiveParamWrapper> getDirectiveParamWrapperList();

  String getName();

}
