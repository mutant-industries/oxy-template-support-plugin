package ool.idea.plugin.file.index.collector;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 1/21/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
abstract public class MacroCollector<E extends PsiElement, V> implements FileBasedIndex.ValueProcessor<V>
{
    protected final Project project;

    protected final LinkedList<E> result;

    public MacroCollector(@NotNull Project project)
    {
        this.result = new LinkedList<E>();
        this.project = project;
    }

    public List<E> getResult()
    {
        return result;
    }

}
