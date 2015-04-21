package ool.idea.plugin.editor.inspection.filter;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.lang.javascript.highlighting.IntentionAndInspectionFilter;
import com.intellij.lang.javascript.inspections.JSCommentMatchesSignatureInspection;
import com.intellij.lang.javascript.inspections.JSDuplicatedDeclarationInspection;
import com.intellij.lang.javascript.inspections.JSUnfilteredForInLoopInspection;
import com.intellij.lang.javascript.inspections.JSUnusedAssignmentInspection;
import com.intellij.lang.javascript.inspections.JSValidateJSDocInspection;
import com.sixrr.inspectjs.style.UnterminatedStatementJSInspection;
import com.sixrr.inspectjs.validity.BadExpressionStatementJSInspection;
import java.util.Arrays;
import java.util.List;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsIntentionAndInspectionFilter extends IntentionAndInspectionFilter
{
    private static final List<String> unsupportedInspectionIDs = Arrays.asList(
            InspectionProfileEntry.getShortName(BadExpressionStatementJSInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(UnterminatedStatementJSInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSUnusedAssignmentInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSDuplicatedDeclarationInspection.class.getSimpleName()),
            // JSDoc has custom handlers
            InspectionProfileEntry.getShortName(JSValidateJSDocInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSCommentMatchesSignatureInspection.class.getSimpleName()),
            InspectionProfileEntry.getShortName(JSUnfilteredForInLoopInspection.class.getSimpleName())
    );

    @Override
    public boolean isSupportedInspection(String inspectionToolId)
    {
        return ! unsupportedInspectionIDs.contains(inspectionToolId);
    }

}
