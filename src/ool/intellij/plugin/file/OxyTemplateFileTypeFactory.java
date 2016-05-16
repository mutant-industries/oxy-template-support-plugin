package ool.intellij.plugin.file;

import ool.intellij.plugin.file.type.OxyTemplateFileType;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * 7/21/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFileTypeFactory extends FileTypeFactory
{
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer)
    {
        consumer.consume(OxyTemplateFileType.INSTANCE, OxyTemplateFileType.DEFAULT_EXTENSION);
    }

}
