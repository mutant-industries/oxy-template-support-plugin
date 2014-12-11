package ool.idea.macro.file;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportFileTypeFactory extends FileTypeFactory
{
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer)
    {
        consumer.consume(MacroSupportFileType.INSTANCE, MacroSupportFileType.DEFAULT_EXTENSION);
    }

}
