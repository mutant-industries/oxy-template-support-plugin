package ool.idea.plugin.editor.annotator;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.validation.JavaScriptAnnotatingVisitor;
import ool.idea.plugin.lang.I18nSupport;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class InnerJsAnnotator extends JavaScriptAnnotatingVisitor
{
    @Override
    protected void checkCallReferences(JSCallExpression node, JSReferenceExpression referenceExpression)
    {
        if((referenceExpression.getText().startsWith("oxy.") || referenceExpression.getText().startsWith("utils.")
                || referenceExpression.getText().startsWith("debug."))&& referenceExpression.resolve() == null)
        {
            myHolder.createWarningAnnotation(referenceExpression, I18nSupport.message("annotator.unresolved.macro.reference.tooltip"));

            return;
        }

        super.checkCallReferences(node, referenceExpression);
    }

}
