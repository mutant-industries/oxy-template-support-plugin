package ool.intellij.plugin.file.index.nacro.js;

import java.util.Objects;

/**
 * 1/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameIndexedElement
{
    private final boolean isMacro;

    private final Integer offsetInFile;

    public JsMacroNameIndexedElement(boolean isMacro, Integer offsetInFile)
    {
        this.isMacro = isMacro;
        this.offsetInFile = offsetInFile;
    }

    public JsMacroNameIndexedElement(Integer offsetInFile)
    {
        this.isMacro = false;
        this.offsetInFile = offsetInFile;
    }

    public boolean isMacro()
    {
        return isMacro;
    }

    public Integer getOffsetInFile()
    {
        return offsetInFile;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isMacro, offsetInFile);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        final JsMacroNameIndexedElement other = (JsMacroNameIndexedElement) obj;

        return Objects.equals(this.isMacro, other.isMacro)
                && Objects.equals(this.offsetInFile, other.offsetInFile);
    }

}
