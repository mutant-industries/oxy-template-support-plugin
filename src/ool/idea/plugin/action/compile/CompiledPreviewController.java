package ool.idea.plugin.action.compile;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.update.MergingUpdateQueue;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import ool.idea.plugin.file.type.CompiledPreviewFileType;
import ool.idea.plugin.file.type.OxyTemplateFileType;
import ool.idea.plugin.file.index.OxyTemplateIndexUtil;
import ool.idea.plugin.lang.CompiledPreview;
import ool.idea.plugin.lang.I18nSupport;
import ool.web.template.exception.CouldNotResolveSourceException;
import ool.web.template.exception.TemplateCompilerException;
import org.antlr.runtime.NoViableAltException;
import org.apache.commons.net.ntp.TimeStamp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
* 2/20/15
*
* @author Petr Mayr <p.mayr@oxyonline.cz>
*/
public class CompiledPreviewController extends AbstractProjectComponent
{
    @NonNls
    private static final String COMPILED_FILE_SUFFIX = ".compiled";

    public static final Key<PsiFile> OFIGINAL_FILE_KEY = Key.create("COMPILED_ORIGINAL");

    private ToolWindow consoleWindow;

    private ConsoleView console;

    private MergingUpdateQueue updateQueue;

    public CompiledPreviewController(Project project)
    {
        super(project);
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return "oxy.template.compiledPreviewController";
    }

    @Override
    public void projectOpened()
    {
        initCompiledCodeUpdater();
        initConsoleView();
    }

    @Override
    public void projectClosed()
    {
        console.dispose();
    }

    public boolean showCompiledCode(@NotNull final PsiFile originalFile)
    {
        Document document = null;
        VirtualFile virtualFile = null;
        PsiFile psiFile = null;
        EditorWindow editorWindow = null;
        boolean result, newTabCreated = false;

        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(myProject);

        // check for already opened editor with compiled source first
        for (FileEditor fileEditor : fileEditorManager.getAllEditors())
        {
            if ( ! (fileEditor instanceof TextEditor))
            {
                continue;
            }

            EditorEx editor = (EditorEx) ((TextEditor) fileEditor).getEditor();
            virtualFile = editor.getVirtualFile();
            psiFile = PsiManager.getInstance(myProject).findFile(virtualFile);

            if(psiFile != null && originalFile.isEquivalentTo(psiFile.getUserData(OFIGINAL_FILE_KEY)))
            {
                document = editor.getDocument();

                // find editor witdow where the file is opened
                for(EditorWindow window : fileEditorManager.getWindows())
                {
                    if(window.findFileIndex(virtualFile) != -1)
                    {
                        editorWindow = window;

                        break;
                    }
                }

                break;
            }
        }

        // create new editor
        if(document == null)
        {
            virtualFile = new LightVirtualFile(originalFile.getName() + COMPILED_FILE_SUFFIX);

            ((LightVirtualFile)virtualFile).setLanguage(CompiledPreview.INSTANCE);
            ((LightVirtualFile)virtualFile).setFileType(CompiledPreviewFileType.INSTANCE);

            psiFile = PsiManager.getInstance(myProject).findFile(virtualFile);

            assert psiFile != null;

            document = PsiDocumentManager.getInstance(myProject).getDocument(psiFile);

            assert document != null;

            psiFile.putUserData(OFIGINAL_FILE_KEY, originalFile);
            document.addDocumentListener(new DocumentAdapter(){
                @Override
                public void documentChanged(DocumentEvent event)
                {
                    PsiFile psiFile = PsiDocumentManager.getInstance(myProject).getPsiFile(event.getDocument());

                    assert psiFile != null;

                    OxyTemplateIndexUtil.triggerReindexing(psiFile);
                }
            });

            editorWindow = fileEditorManager.getCurrentWindow();
            newTabCreated = true;
        }

        assert editorWindow != null;

        if(result = showCompiledCode(originalFile, document))
        {
            if(newTabCreated)
            {
                editorWindow.split(SwingConstants.HORIZONTAL, false, virtualFile, false);
                OxyTemplateIndexUtil.triggerReindexing(psiFile);
            }
            if(editorWindow.findFileIndex(virtualFile) == -1)
            {
                fileEditorManager.openFile(virtualFile, false);
            }

            // switch to editor with recompiled source
            editorWindow.setEditor(editorWindow.findFileComposite(virtualFile), false);
        }

        return result;
    }

    public boolean showCompiledCode(@NotNull final PsiFile originalFile, @NotNull  final Document document)
    {
        PsiDocumentManager.getInstance(myProject).commitAllDocuments();
        long startTime = System.currentTimeMillis();

        try
        {
            showCompiledCode(document, CompiledPreviewUtil.buildCompiledCode(originalFile));
        }
        catch (TemplateCompilerException e)
        {
            onCompilerError(e, originalFile.getVirtualFile());

            return false;
        }
        catch (RuntimeException e)
        {
            onCompilerError(e, originalFile.getVirtualFile());

            return false;
        }
        catch (CouldNotResolveSourceException e)
        {
            throw new AssertionError("Should not happen", e);
        }

        console.print(I18nSupport.message("action.live.update.compilation.success", originalFile.getVirtualFile().getPath(),
                System.currentTimeMillis() - startTime) + '\n', ConsoleViewContentType.NORMAL_OUTPUT);

        return true;
    }

    public void showCompiledCode(@NotNull final Document document, @NotNull final String source)
    {
        CommandProcessor.getInstance().executeCommand(myProject, new Runnable()
        {
            @Override
            public void run()
            {
                ApplicationManager.getApplication().runWriteAction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        document.setText(source);
                        PsiDocumentManager.getInstance(myProject).commitDocument(document);
                    }
                });
            }
        }, I18nSupport.message("action.live.update"), "oxy-compiled-template-live-update-" + TimeStamp.getCurrentTime());
    }

    // ----------------------------------------------------------------------------------------------------
    private void initCompiledCodeUpdater()
    {
        updateQueue = new MergingUpdateQueue("LIVE_PREVIEW_QUEUE", 1000, true, null, myProject);

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentAdapter()
        {
            @Override
            public void documentChanged(DocumentEvent e)
            {
                Document document = e.getDocument();
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);

                if (file == null || file.getFileType() != OxyTemplateFileType.INSTANCE)
                {
                    return;
                }

                updateQueue.cancelAllUpdates();

                updateQueue.queue(new CompiledPreviewUpdater(Boolean.TRUE, CompiledPreviewController.this, myProject));
            }
        }, myProject);
    }

    private void initConsoleView()
    {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);

        TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
        TextConsoleBuilder consoleBuilder = factory.createBuilder(myProject);
        console = consoleBuilder.getConsole();

        JComponent consoleComponent = console.getComponent();
        Content content = contentFactory.createContent(consoleComponent, "", false);

        consoleWindow = toolWindowManager.registerToolWindow(I18nSupport.message("action.live.update.compiler.output"), true, ToolWindowAnchor.BOTTOM);
        consoleWindow.getContentManager().addContent(content);
        consoleWindow.setIcon(OxyTemplateFileType.INSTANCE.getIcon());
    }

    private void onCompilerError(@NotNull final Throwable exception, @NotNull final VirtualFile virtualFile)
    {
        if(exception instanceof RuntimeException && ! (exception.getCause() instanceof NoViableAltException))
        {
            throw (RuntimeException)exception;
        }

        console.print(I18nSupport.message("action.live.update.compilation.error", virtualFile.getPath()) + ":\n",
                ConsoleViewContentType.NORMAL_OUTPUT);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        exception.printStackTrace(printWriter);

        consoleWindow.show(null);
        console.print(stringWriter.toString(), ConsoleViewContentType.ERROR_OUTPUT);
        printWriter.close();
    }

}
