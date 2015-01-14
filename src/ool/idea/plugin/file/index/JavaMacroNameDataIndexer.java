package ool.idea.plugin.file.index;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JavaMacroNameDataIndexer implements DataIndexer<String, Void, FileContent>
{
    private static final Pattern MACRO_NAME_PATTERN = Pattern
            .compile("class\\s+(\\w+)Macro\\s+(extends\\s+[a-zA-Z_<>]+\\s+){0,1}implements\\s+Macro");

    @Override
    @NotNull
    public Map<String, Void> map(@NotNull final FileContent inputData)
    {
        String content = inputData.getContentAsText().toString();

        Matcher matcher = MACRO_NAME_PATTERN.matcher(content);

        if(matcher.find())
        {
            return Collections.singletonMap(StringUtil.decapitalize(matcher.group(1)), null);
        }

        return Collections.emptyMap();
    }

}
