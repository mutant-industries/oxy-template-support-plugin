package ool.intellij.plugin.file;

import java.util.HashSet;
import java.util.Set;

import ool.intellij.plugin.lang.OxyTemplate;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;
import ool.intellij.plugin.psi.OxyTemplateInnerJsElementType;
import ool.intellij.plugin.psi.OxyTemplateTypes;

import com.google.common.collect.Sets;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.ConfigurableTemplateLanguageFileViewProvider;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider
        implements ConfigurableTemplateLanguageFileViewProvider
{
    private static final TemplateDataElementType TEMPLATE_MARKUP_DATA_TYPE = new TemplateDataElementType("TEMPLATE_MARKUP", OxyTemplate.INSTANCE,
            OxyTemplateTypes.T_TEMPLATE_HTML_CODE, OxyTemplateTypes.T_OUTER_TEMPLATE_ELEMENT);

    private static final TemplateDataElementType TEMPLATE_INNERJS_DATA_TYPE = new OxyTemplateInnerJsElementType("TEMPLATE_JS", OxyTemplateInnerJs.INSTANCE,
            OxyTemplateTypes.T_TEMPLATE_JAVASCRIPT_CODE, OxyTemplateTypes.T_INNER_TEMPLATE_ELEMENT);

    private static final HashSet<Language> LANGUAGES = Sets.newHashSet(OxyTemplate.INSTANCE,
            OxyTemplateInnerJs.INSTANCE, HTMLLanguage.INSTANCE);

    @NotNull
    private final PsiManager psiManager;

    @NotNull
    private final VirtualFile virtualFile;

    public OxyTemplateFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file, boolean physical)
    {
        super(manager, file, physical);

        psiManager = manager;
        virtualFile = file;
    }

    private Language getTemplateDataLanguage(PsiManager manager, VirtualFile file)
    {
        // get the main language of the file
        Language dataLang = TemplateDataLanguageMappings.getInstance(manager.getProject()).getMapping(file);
        if (dataLang == null)
        {
            dataLang = OxyTemplate.getDefaultTemplateLang().getLanguage();
        }

        Language substituteLang = LanguageSubstitutors.getInstance().substituteLanguage(dataLang, file, manager.getProject());

        // only use a substituted language if it's templateable
        if (TemplateDataLanguageMappings.getTemplateableLanguages().contains(substituteLang))
        {
            dataLang = substituteLang;
        }

        return dataLang;
    }

    @NotNull
    @Override
    public Language getBaseLanguage()
    {
        return OxyTemplate.INSTANCE;
    }

    @NotNull
    @Override
    public Language getTemplateDataLanguage()
    {
        return getTemplateDataLanguage(psiManager, virtualFile);
    }

    @NotNull
    @Override
    protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile fileCopy)
    {
        return new OxyTemplateFileViewProvider(getManager(), fileCopy, false);
    }

    @NotNull
    @Override
    public Set<Language> getLanguages()
    {
        return LANGUAGES;
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Language lang)
    {
        ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(lang);

        if (parserDefinition == null)
        {
            return null;
        }

        Language templateDataLanguage = getTemplateDataLanguage(psiManager, virtualFile);

        if (lang == templateDataLanguage)
        {
            PsiFileImpl file = (PsiFileImpl) parserDefinition.createFile(this);
            file.setContentElementType(TEMPLATE_MARKUP_DATA_TYPE);

            return file;
        }
        else if (lang == OxyTemplate.INSTANCE)
        {
            return parserDefinition.createFile(this);
        }
        else if (lang == OxyTemplateInnerJs.INSTANCE)
        {
            PsiFileImpl file = (PsiFileImpl) parserDefinition.createFile(this);
            file.setContentElementType(TEMPLATE_INNERJS_DATA_TYPE);

            return file;
        }

        return null;
    }

}
