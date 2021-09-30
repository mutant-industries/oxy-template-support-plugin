// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;

public interface MacroTag extends MacroCall {

  @NotNull
  List<BlockCloseStatement> getBlockCloseStatementList();

  @NotNull
  List<BlockOpenStatement> getBlockOpenStatementList();

  @NotNull
  List<DirectiveStatement> getDirectiveStatementList();

  @NotNull
  List<MacroAttribute> getMacroAttributeList();

  @NotNull
  List<MacroEmptyTag> getMacroEmptyTagList();

  @NotNull
  List<MacroName> getMacroNameList();

  @NotNull
  List<MacroTag> getMacroTagList();

  @NotNull
  List<MacroXmlPrefix> getMacroXmlPrefixList();

  @NotNull
  MacroName getMacroName();

  @NotNull TextRange getContentRange();

}
