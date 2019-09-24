package ool.intellij.plugin.file.index.nacro.js;

import java.util.HashMap;
import java.util.Map;

import ool.intellij.plugin.file.index.nacro.MacroIndex;
import ool.intellij.plugin.lang.OxyTemplateInnerJs;

import com.intellij.lang.javascript.psi.JSAssignmentExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsMacroNameDataIndexer extends MacroIndex implements DataIndexer<String, JsMacroNameIndexedElement, FileContent>
{
    @Override
    @NotNull
    public Map<String, JsMacroNameIndexedElement> map(@NotNull final FileContent inputData)
    {
        PsiFile jsFile = inputData.getPsiFile().getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE);

        Map<String, JsMacroNameIndexedElement> result = new HashMap<>();

        for (PsiElement psiElement : jsFile.getChildren())
        {
            if (psiElement instanceof JSExpressionStatement
                    && (psiElement = PsiTreeUtil.getChildOfAnyType(psiElement, JSAssignmentExpression.class)) != null
                    && psiElement.getFirstChild() instanceof JSDefinitionExpression)
            {
                String rootNamespace = psiElement.getFirstChild().getText().replace(MACRO_REGISTRY_NAMESPACE + ".", "");
                boolean firstIteration = true;

                for (JSReferenceExpression ref : PsiTreeUtil.findChildrenOfType(psiElement.getFirstChild(),
                        JSReferenceExpression.class))
                {
                    String namespace = ref.getText().replace(MACRO_REGISTRY_NAMESPACE + ".", "");

                    if ( ! namespace.equals(DEFAULT_NAMESPACE) && ! namespace.equals(MACRO_REGISTRY_NAMESPACE))
                    {
                        result.put(ref.getText().replace(MACRO_REGISTRY_NAMESPACE + ".", ""), new JsMacroNameIndexedElement(firstIteration &&
                                psiElement.getLastChild() instanceof JSFunctionExpression, ref.getTextOffset() + ref.getTextLength() - 1));
                    }

                    firstIteration = false;
                }

                if (psiElement.getLastChild() instanceof JSObjectLiteralExpression)
                {
                    processObjectLiteralExpression((JSObjectLiteralExpression) psiElement.getLastChild(), rootNamespace, result);
                }
            }
        }

        return result;
    }

    private static void processObjectLiteralExpression(@NotNull JSObjectLiteralExpression expression, @NotNull String namespace,
                                                       @NotNull Map<String, JsMacroNameIndexedElement> result)
    {
        for (JSProperty property : expression.getProperties())
        {
            PsiElement nameIdentifier = property.getNameIdentifier();

            if (nameIdentifier == null)
            {
                continue;
            }

            if (property.getLastChild() instanceof JSObjectLiteralExpression)
            {
                result.put(namespace + "." + nameIdentifier.getText().replace(MACRO_REGISTRY_NAMESPACE + ".", ""),
                        new JsMacroNameIndexedElement(nameIdentifier.getTextOffset() + nameIdentifier.getTextLength() - 1));

                processObjectLiteralExpression((JSObjectLiteralExpression) property.getLastChild(),
                        namespace + "." + property.getName(), result);
            }
            else if (property.getLastChild() instanceof JSFunctionExpression)
            {
                result.put(namespace + "." + nameIdentifier.getText().replace(MACRO_REGISTRY_NAMESPACE + ".", ""),
                        new JsMacroNameIndexedElement(true, nameIdentifier.getTextOffset() + nameIdentifier.getTextLength() - 1));
            }
        }
    }

}
