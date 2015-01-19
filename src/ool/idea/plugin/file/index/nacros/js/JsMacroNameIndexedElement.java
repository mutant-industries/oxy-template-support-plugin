package ool.idea.plugin.file.index.nacros.js;

import com.google.common.base.Objects;

/**
 * 1/15/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameIndexedElement
{
    private final boolean isMacro;

    private final Integer offserInFile;

    public JsMacroNameIndexedElement(boolean isMacro, Integer offserInFile)
    {
        this.isMacro = isMacro;
        this.offserInFile = offserInFile;
    }

    public JsMacroNameIndexedElement(Integer offserInFile)
    {
        this.isMacro = false;
        this.offserInFile = offserInFile;
    }

    public boolean isMacro()
    {
        return isMacro;
    }

    public Integer getOffserInFile()
    {
        return offserInFile;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(isMacro, offserInFile);
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

        return Objects.equal(this.isMacro, other.isMacro)
                && Objects.equal(this.offserInFile, other.offserInFile);
    }

}
