// This is a generated file. Not intended for manual editing.
package ool.intellij.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface MacroName extends OxyTemplatePsiElement {

  @Nullable PsiReference getReference();

  @NotNull PsiReference[] getReferences();

  PsiElement setName(@NotNull String newName);

  @NotNull String getName();

  boolean isClosingTagMacroName();

}
