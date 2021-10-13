package ool.intellij.plugin.action.compile;

import ool.template.core.CompilationContext;
import ool.template.core.exception.CouldNotResolveSourceException;
import ool.template.core.exception.TemplateCompilerException;
import ool.template.core.impl.Preprocessor;
import ool.template.core.source.Source;

import com.intellij.psi.PsiFile;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 2/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class CompiledPreviewUtil
{
    @NotNull
    public static CharSequence buildCompiledCode(@NotNull PsiFile file)
            throws CouldNotResolveSourceException, TemplateCompilerException
    {
        String path = file.getVirtualFile().getCanonicalPath();

        String parentPath = FilenameUtils.getFullPath(path);
        String templateName = FilenameUtils.getName(path);

        assert templateName != null;
        assert parentPath != null;

        Preprocessor preprocessor = new Preprocessor();

        Source source = new VirtualFileSourceResolver(parentPath)
                .resolveSource(templateName);
        CompilationContext ctx = new CompilationContext(preprocessor, source, templateName);
        preprocessor.produceCode(ctx);

        return preprocessor.getResult();
    }

}
