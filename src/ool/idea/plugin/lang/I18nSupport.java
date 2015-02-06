package ool.idea.plugin.lang;

import com.intellij.CommonBundle;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * 2/4/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public class I18nSupport
{
    @NonNls
    private static final String BUNDLE = "I18n.messages";

    private static Reference<ResourceBundle> oxyTemplateSupportBundle;

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params)
    {
        return CommonBundle.message(getBundle(), key, params);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private static ResourceBundle getBundle()
    {
        ResourceBundle bundle;

        if (oxyTemplateSupportBundle != null && (bundle = oxyTemplateSupportBundle.get()) != null)
        {
            return bundle;
        }
        else
        {
            bundle = ResourceBundle.getBundle(BUNDLE);

            oxyTemplateSupportBundle = new SoftReference(bundle);
        }

        return bundle;
    }

}
