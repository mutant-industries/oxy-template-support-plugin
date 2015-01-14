package ool.idea.plugin.editor.inspection;

import com.intellij.lang.javascript.highlighting.IntentionAndInspectionFilter;
import com.sixrr.inspectjs.validity.BadExpressionStatementJSInspection;
import org.jetbrains.annotations.NotNull;

/**
 * 1/9/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsIntentionAndInspectionFilter extends IntentionAndInspectionFilter
{
    @Override
    public boolean isSupported(@NotNull Class clazz)
    {
        return ! clazz.equals(BadExpressionStatementJSInspection.class);
                // && ! clazz.equals(UnterminatedStatementJSInspection.class);
    }

//    @Override
//    public boolean isSupportedInspection(String inspectionToolId)
//    {
//        return ! inspectionToolId.equals(InspectionProfileEntry.getShortName(BadExpressionStatementJSInspection.class.getSimpleName()));
//                // && ! inspectionToolId.equals(InspectionProfileEntry.getShortName(UnterminatedStatementJSInspection.class.getSimpleName()));
//    }

}
