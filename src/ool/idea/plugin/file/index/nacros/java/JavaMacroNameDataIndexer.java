package ool.idea.plugin.file.index.nacros.java;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroNameDataIndexer extends MacroIndex implements DataIndexer<String, Void, FileContent>
{
    @NonNls
    private static final String MACRO_INTERFACE_FQN = "ool.web.template.Macro";
    @NonNls
    private static final List<String> macrosInDebugNamespace = Arrays.asList("generatedCode");

    @Override
    @NotNull
    public Map<String, Void> map(@NotNull final FileContent inputData)
    {
        PsiJavaFile file = (PsiJavaFile)inputData.getPsiFile();

        PsiClass macroInterface = JavaPsiFacade.getInstance(inputData.getProject())
                .findClass(MACRO_INTERFACE_FQN, ProjectScope.getAllScope(inputData.getProject()));

        if(macroInterface == null)
        {
            return Collections.emptyMap();
        }

        for(PsiClass psiClass : file.getClasses())
        {
            if(psiClass.isInterface() || Arrays.asList(psiClass.getModifierList()).contains(PsiModifier.ABSTRACT)
                    || ! psiClass.isInheritor(macroInterface, true)
                    || ! psiClass.getName().matches(".+Macro$"))
            {
                continue;
            }

            String macroName = StringUtil.decapitalize(psiClass.getName().replaceFirst("Macro$", ""));

            return Collections.singletonMap((macrosInDebugNamespace.contains(macroName) ?
                    DEBUG_NAMESPACE : DEFAULT_NAMESPACE) + "." + macroName, null);
        }

        return Collections.emptyMap();
    }

}
