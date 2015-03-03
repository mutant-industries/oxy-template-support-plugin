package ool.idea.plugin.action.compile;

import com.intellij.psi.PsiFile;
import ool.web.template.CompilationContext;
import ool.web.template.exception.CouldNotResolveSourceException;
import ool.web.template.exception.TemplateCompilerException;
import ool.web.template.impl.Preprocessor;
import ool.web.template.source.Source;
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
