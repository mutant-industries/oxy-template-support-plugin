package ool.idea.plugin.editor.highlighter;

import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.JSFlexAdapter;
import com.intellij.lang.javascript.highlighting.JSHighlighter;
import com.intellij.lexer.FlexLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import ool.idea.plugin.lang.lexer.OxyTemplateInnerJsLexerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * 2/2/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class OxyTemplateInnerJsHighlighter extends JSHighlighter
{
    private final Logger logger = Logger.getInstance(getClass());

    public OxyTemplateInnerJsHighlighter(DialectOptionHolder dialectOptionsHolder)
    {
        super(dialectOptionsHolder);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer()
    {
        Lexer lexer = super.getHighlightingLexer();

        // js library hack ------------------
        try
        {
            Field myDelegateField = lexer.getClass().getSuperclass().getSuperclass().getDeclaredField("myDelegate");
            myDelegateField.setAccessible(true);

            final Class clazz = Class.forName("com.intellij.lang.javascript._JavaScriptLexer");

            @SuppressWarnings("unchecked")
            Constructor constructor = clazz.getConstructor(Boolean.TYPE, DialectOptionHolder.class);
            constructor.setAccessible(true);

            FlexLexer flex = (FlexLexer)constructor.newInstance(true, getDialectOptionsHolder());

            myDelegateField.set(lexer, new OxyTemplateInnerJsLexerAdapter(new JSFlexAdapter(flex)));
        }
        catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e)
        {
            logger.error("Javascript api change !", e);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            logger.error(e);
        }
        // ----------------------------------

        return lexer;
    }

}
