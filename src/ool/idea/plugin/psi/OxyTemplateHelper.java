package ool.idea.plugin.psi;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.JSRecursiveElementVisitor;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.templateLanguages.TreePatcher;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlElementType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.file.index.collector.IncludedFilesCollector;
import ool.idea.plugin.file.index.nacros.MacroIndex;
import ool.idea.plugin.lang.OxyTemplateInnerJs;
import ool.idea.plugin.lang.parser.definition.OxyTemplateParserDefinition;
import ool.idea.plugin.psi.visitor.MacroNameVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 2/3/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class OxyTemplateHelper
{
    @NotNull
    public static Map<DirectiveParamFileReference, VirtualFile> getIncludedFiles(@NotNull PsiFile psiFile)
    {
        IncludedFilesCollector collector = new IncludedFilesCollector();

        return collector.collect(psiFile).getResult();
    }

    @NotNull
    public static Map<PsiElement, JSElement> getUsedJsMacros(@NotNull PsiFile psiFile)
    {
        final Map<PsiElement, JSElement> usedMacros = new HashMap<PsiElement, JSElement>();

        new MacroNameVisitor()
        {
            @Override
            public void visitMacroName(@NotNull MacroName macroName)
            {
                PsiElement reference;

                if (macroName.getReference() != null && (reference = macroName.getReference().resolve()) != null
                        && OxyTemplateIndexUtil.getJsMacroNameReferences(macroName.getName(), macroName.getProject()).size() > 0)
                {
                    usedMacros.put(macroName, (JSElement) reference);
                }
            }
        }.visitFile(psiFile);

        new JSRecursiveElementVisitor()
        {
            @Override
            public void visitJSCallExpression(@NotNull JSCallExpression node)
            {
                if( ! (node.getFirstChild() instanceof JSReferenceExpression))
                {
                    return;
                }

                JSReferenceExpression referenceExpression = (JSReferenceExpression) node.getFirstChild();
                PsiElement reference;

                if (referenceExpression.getReference() != null && (reference = referenceExpression.getReference().resolve())  != null
                        && OxyTemplateIndexUtil.getJsMacroNameReferences(MacroIndex.normalizeMacroName(referenceExpression.getText()),
                            referenceExpression.getProject()).size() > 0)
                {
                    usedMacros.put(referenceExpression, (JSElement) reference);
                }
            }
        }.visitFile(psiFile.getViewProvider().getPsi(OxyTemplateInnerJs.INSTANCE));

        return usedMacros;
    }

    public static boolean isJsMacroMissingInclude(@NotNull PsiFile file, @NotNull PsiElement macro)
    {
        return ! macro.getContainingFile().getVirtualFile().equals(file.getVirtualFile())
                && ! getIncludedFiles(file).values().contains(macro.getContainingFile().getVirtualFile());
    }

    /**
     * @param elementAt anything between opening and closing tag name
     * @return
     */
    @Nullable
    public static String getPreviousUnclosedMacroTagName(@Nullable final PsiElement elementAt)
    {
        if(elementAt == null)
        {
            return null;
        }

        PsiElement psiElement = elementAt;

        do
        {
            if(psiElement instanceof MacroName)
            {
                String name = psiElement.getText();

                psiElement = elementAt;

                while((psiElement = psiElement.getNextSibling()) != null)
                {
                    if(psiElement instanceof MacroName)
                    {
                        if(psiElement.getText().equals(name))
                        {
                            if((psiElement = PsiTreeUtil.getTopmostParentOfType(elementAt, MacroTag.class)) != null
                                    && ((MacroTag) psiElement).getMacroNameList().size() == 1)
                            {
                                return name;
                            }

                            return null;
                        }

                        return name;
                    }
                }

                return name;
            }
        }
        while((psiElement = psiElement.getPrevSibling()) != null);

        return null;
    }

    public static void addDirective(@NotNull DirectiveStatement directiveStatement, @NotNull PsiFile file)
    {
        List<DirectiveStatement> statements = PsiTreeUtil.getChildrenOfTypeAsList(file, DirectiveStatement.class);

        if(statements.size() > 0)
        {
            PsiElement nextSibling = statements.get(statements.size() - 1).getNextSibling();

            if(nextSibling instanceof PsiWhiteSpace)
            {
                nextSibling = nextSibling.getNextSibling();
            }

            if(nextSibling != null && OxyTemplateParserDefinition.COMMENTS.contains(nextSibling.getNode().getElementType()))
            {
                file.addAfter(directiveStatement, nextSibling);
            }
            else
            {
                file.addAfter(directiveStatement, statements.get(statements.size() - 1));
            }
        }
        else
        {
            file.addBefore(directiveStatement, file.getFirstChild());
        }
    }

    @Nullable
    public static ResolveResult multiResolveWithIncludeSearch(@NotNull PsiElement referencing, @NotNull ResolveResult[] references)
    {
        Collection<VirtualFile> includedFiles = OxyTemplateHelper.getIncludedFiles(referencing
                .getContainingFile()).values();

        for(ResolveResult result : references)
        {
            if(includedFiles.contains(result.getElement().getContainingFile().getVirtualFile()))
            {
                return result;
            }
        }

        return null;
    }

    /**
     * Handles insertion of foreign elements (T_OUTER_TEMPLATE_ELEMENT, T_INNER_TEMPLATE_ELEMENT) into js / html tree.
     * Element is inserted before topmost parent with the same start offset as the offset of block before which
     * it would be originally inserted. It means, that foreign element never becomes child of for example js var
     * statement or xml attribute which would happen for example in this case:
     *
     * <div <m:foo>data-id="data"</m:foo>>
     *
     * </div>
     *
     *  HtmlTag:div(0,43)
     *    XmlToken:XML_START_TAG_START('<')(0,1)
     *    XmlToken:XML_NAME('div')(1,4)
     *    PsiWhiteSpace(' ')(4,5)
     *    PsiElement(XML_ATTRIBUTE)(5,26)
     *      PsiElement(Outer Template Element)('<m:foo>')(5,12)     <-
     *      XmlToken:XML_NAME('data-id')(12,19)
     *      XmlToken:XML_EQ('=')(19,20)
     *      PsiElement(XML_ATTRIBUTE_VALUE)(20,26)
     *        XmlToken:XML_ATTRIBUTE_VALUE_START_DELIMITER('"')(20,21)
     *        XmlToken:XML_ATTRIBUTE_VALUE_TOKEN('data')(21,25)
     *        XmlToken:XML_ATTRIBUTE_VALUE_END_DELIMITER('"')(25,26)
     *    PsiElement(Outer Template Element)('</m:foo>')(26,34)
     *    XmlToken:XML_TAG_END('>')(34,35)
     *    XmlText(35,37)
     *      PsiWhiteSpace('\n\n')(35,37)
     *    XmlToken:XML_END_TAG_START('</')(37,39)
     *    XmlToken:XML_NAME('div')(39,42)
     *    XmlToken:XML_TAG_END('>')(42,43)
     *
     * @param parent
     * @param anchorBefore
     * @param toInsert
     * @return true if patch was applied, false otherwise
     * @see TreePatcher
     */
    public static boolean insertOuterElementToAST(CompositeElement parent, TreeElement anchorBefore, OuterLanguageElement toInsert)
    {
        int parentStartOffset = parent.getStartOffset();

        if(anchorBefore != null && anchorBefore.getStartOffset() == parentStartOffset && ! (parent instanceof FileElement))
        {
            while(parent.getTreeParent() != null &&  ! (parent.getTreeParent() instanceof FileElement)
                    && parent.getTreeParent().getStartOffset() == parentStartOffset)
            {
                parent = parent.getTreeParent();
            }

            parent.rawInsertBeforeMe((TreeElement)toInsert);

            return true;
        }

        return false;
    }

    public static boolean containsElement(@NotNull PsiElement element, IElementType... types)
    {
        return containsElement(element, null, types);
    }

    public static boolean containsElement(@NotNull PsiElement element, @Nullable TextRange textRange, IElementType... types)
    {
        ChildElementFinder childElementFinder = new ChildElementFinder(types);
        childElementFinder.setAllowedRange(textRange);
        element.accept(childElementFinder);

        return childElementFinder.getResult() != null;
    }

    public static boolean checkRangeContainsParent(@NotNull PsiElement element, @NotNull TextRange allowedRange,
                                                   @NotNull IElementType outerLanguageElement)
    {
        PsiElement parent = element.getParent();

        if(parent == null || parent instanceof PsiFile || parent.getTextRange() == null
                || parent.getNode().getElementType() == XmlElementType.HTML_DOCUMENT || element instanceof PsiWhiteSpace)
        {
            return false;
        }
        if( ! allowedRange.contains(parent.getTextRange()))
        {
            return false;
        }
        if(allowedRange.equals(parent.getTextRange()))
        {
            if(parent.getPrevSibling() != null && parent.getNextSibling() != null
                    && parent.getPrevSibling().getNode().getElementType() == outerLanguageElement
                    && parent.getNextSibling().getNode().getElementType() == outerLanguageElement)
            {
                /**
                 * parent covers fully allowed range, both siblings are outer language element type, e.g.
                 * <m:foo><br/></m:foo>
                 */
                return true;
            }
            if((parent.getPrevSibling() == null || parent.getPrevSibling().getNode().getElementType() == XmlElementType.XML_PROLOG)
                    && parent.getNextSibling() == null)
            {
                /**
                 * parent is the unique root element in a file (has no siblings)
                 */
                return true;
            }

            return false;
        }

        return true;
    }

}
