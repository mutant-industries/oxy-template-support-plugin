package ool.idea.plugin.file.index.globals;

import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * 1/13/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class JsGlobalsDataIndexer implements DataIndexer<String, Integer, FileContent>
{
    private static final Pattern GLOBAL_MODEL_PROVIDER_SUBCLASS = Pattern.compile("class\\s+\\w+\\s+implements\\s+GlobalModelProvider");
    private static final Pattern MODEL_PROVIDER_REGISTRY_PARAM = Pattern.compile("(get){0,1}[mM]odelProviderRegistry\\s*(\\(\\s*\\)){0,1}\\s*\\.register\\s*\\(\\s*");
    private static final Pattern GLOBAL_MODEL_PROVIDER_SUBCLASS_GET_NAME_RETURN_VALUE = Pattern.compile("public\\s+String\\s+getName\\s*\\(\\s*\\)\\s*\\{\\s*return\\s*");

    @Override
    @NotNull
    public Map<String, Integer> map(@NotNull final FileContent inputData)
    {
        String content = inputData.getContentAsText().toString();
        Map<String, Integer> result = new HashMap<String, Integer>();
        Matcher matcher;

        matcher = GLOBAL_MODEL_PROVIDER_SUBCLASS.matcher(content);

        if(matcher.find())
        {
            matcher = GLOBAL_MODEL_PROVIDER_SUBCLASS_GET_NAME_RETURN_VALUE.matcher(content);
        }
        else
        {
            matcher = MODEL_PROVIDER_REGISTRY_PARAM.matcher(content);
        }

        while(matcher.find())
        {
            StringBuilder name = new StringBuilder();

            int end = matcher.end() + 1;

            while(end < content.length() && content.charAt(end) != '"')
            {
                name.append(content.charAt(end));
                end++;
            }

            result.put(name.toString(), matcher.end());
        }

        return result;
    }

}
