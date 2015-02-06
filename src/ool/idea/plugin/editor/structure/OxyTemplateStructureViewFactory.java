package ool.idea.plugin.editor.structure;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.javascript.structureView.JSStructureViewBuilderFactory;
import com.intellij.psi.PsiFile;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import org.jetbrains.annotations.Nullable;

/**
 * 1/24/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateStructureViewFactory extends JSStructureViewBuilderFactory
{
    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile)
    {
        return super.getStructureViewBuilder(psiFile.getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE));
    }

}
