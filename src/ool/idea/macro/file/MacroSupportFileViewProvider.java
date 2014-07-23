package ool.idea.macro.file;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.ConfigurableTemplateLanguageFileViewProvider;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import gnu.trove.THashSet;
import java.util.Arrays;
import java.util.Set;
import ool.idea.macro.MacroSupport;
import ool.idea.macro.psi.MacroSupportTypes;
import org.jetbrains.annotations.NotNull;

/**
 * 7/23/14
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class MacroSupportFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider
        implements ConfigurableTemplateLanguageFileViewProvider
{
    @NotNull private final PsiManager myManager;
    @NotNull private final VirtualFile myVirtualFile;

    public MacroSupportFileViewProvider(PsiManager manager, VirtualFile file, boolean physical) {
        super(manager, file, physical);

        myManager = manager;
        myVirtualFile = file;

        getTemplateDataLanguage(myManager, myVirtualFile);
    }

    private Language getTemplateDataLanguage(PsiManager manager, VirtualFile file) {
        // get the main language of the file
        Language dataLang = TemplateDataLanguageMappings.getInstance(manager.getProject()).getMapping(file);
        if(dataLang == null) {
            dataLang = MacroSupport.getDefaultTemplateLang().getLanguage();
        }

        Language substituteLang = LanguageSubstitutors.INSTANCE.substituteLanguage(dataLang, file, manager.getProject());

        // only use a substituted language if it's templateable
        if (TemplateDataLanguageMappings.getTemplateableLanguages().contains(substituteLang)) {
            dataLang = substituteLang;
        }

        return dataLang;
    }

    @NotNull
    @Override
    public Language getBaseLanguage()
    {
        return MacroSupport.getInstance();
    }

    @NotNull
    @Override
    public Language getTemplateDataLanguage()
    {
        return getTemplateDataLanguage(myManager, myVirtualFile);
    }

    @Override
    protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(VirtualFile fileCopy)
    {
        return new MacroSupportFileViewProvider(getManager(), fileCopy, false);
    }

    @NotNull
    @Override
    public Set<Language> getLanguages() {
        return new THashSet<Language>(Arrays.asList(new Language[]{MacroSupport.getInstance(), getTemplateDataLanguage(myManager, myVirtualFile)}));
    }

    @Override
    protected PsiFile createFile(@NotNull Language lang) {
        ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(lang);
        if (parserDefinition == null) {
            return null;
        }

        Language templateDataLanguage = getTemplateDataLanguage(myManager, myVirtualFile);
        if (lang == templateDataLanguage) {
            PsiFileImpl file = (PsiFileImpl) parserDefinition.createFile(this);
            file.setContentElementType(new TemplateDataElementType("MACRO_TEMPLATE_DATA", templateDataLanguage, MacroSupportTypes.TEMPLATE_HTML_TEXT, MacroSupportTypes.TEMPLATE_JAVASCRIPT_TEXT));
            return file;
        } else if (lang == MacroSupport.getInstance()) {
            return parserDefinition.createFile(this);
        } else {
            return null;
        }
    }

}
