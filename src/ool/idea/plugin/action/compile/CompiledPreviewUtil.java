package ool.idea.plugin.action.compile;

import com.intellij.psi.PsiFile;
import java.io.File;
import ool.web.template.CompilationContext;
import ool.web.template.exception.CouldNotResolveSourceException;
import ool.web.template.exception.TemplateCompilerException;
import ool.web.template.impl.Code;
import ool.web.template.impl.Preprocessor;
import ool.web.template.source.FileResolver;
import ool.web.template.source.Source;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
* 2/20/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class CompiledPreviewUtil
{
    public static final int SOURCE_BUILDER_DEFAULT_CAPACITY = 1024 * 1024;

    /**
     * duplicate to {@link ool.web.template.TemplateCompiler#compile(ool.web.template.CompilationContext)},
     *  should be performed in readAction
     */
    @NotNull
    public static String buildCompiledCode(@NotNull PsiFile file)
            throws CouldNotResolveSourceException, TemplateCompilerException
    {
        String path = file.getVirtualFile().getCanonicalPath();

        String parentPath = FilenameUtils.getFullPath(path);
        String templateName = FilenameUtils.getName(path);

        assert templateName != null;
        assert parentPath != null;

        StringBuilder builder = new StringBuilder(SOURCE_BUILDER_DEFAULT_CAPACITY);
        Code code;

        Source source = new FileResolver(new File(parentPath))
                .resolveSource(templateName);
        CompilationContext ctx = new CompilationContext(Preprocessor.getInstance(), source, templateName);
        code = ctx.getPreprocessor().produceCode(ctx);

        for (Code.Line line : code.getLines())
        {
            builder.append(line.getContent()).append("\n");
        }

        return builder.toString();
    }

}
