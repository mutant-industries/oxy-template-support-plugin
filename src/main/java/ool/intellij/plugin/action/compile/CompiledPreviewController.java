package ool.intellij.plugin.action.compile;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.*;

import ool.intellij.plugin.file.type.CompiledPreviewFileType;
import ool.intellij.plugin.file.type.OxyTemplateFileType;
import ool.intellij.plugin.lang.CompiledPreview;
import ool.intellij.plugin.lang.I18nSupport;
import ool.web.template.exception.CouldNotResolveSourceException;
import ool.web.template.exception.ErrorInformation;
import ool.web.template.exception.TemplateCompilerException;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileEditor.impl.EditorWithProviderComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.update.MergingUpdateQueue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * 2/20/15
 *
 * @author Petr Mayr <p.mayr@oxyonline.cz>
 */
public final class CompiledPreviewController implements Disposable
{
    @NonNls
    private static final String COMPILED_FILE_SUFFIX = ".compiled";

    public static final Key<PsiFile> ORIGINAL_FILE_KEY = Key.create("COMPILED_ORIGINAL");

    private final Project myProject;

    private final ConsoleView console;

    private final NotificationGroup compilerNotificationGroup;

    private final DocumentListener documentUpdateListener;

    private final MergingUpdateQueue updateQueue;

    private ToolWindow consoleWindow;

    public CompiledPreviewController(@NotNull Project project)
    {
        myProject = project;
        compilerNotificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("oxy.CompilerMessages");

        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(myProject);

        console = consoleBuilder.getConsole();

        updateQueue = new MergingUpdateQueue("LIVE_PREVIEW_QUEUE", 1000, true, null, myProject);

        documentUpdateListener = new DocumentListener()
        {
            @Override
            public void documentChanged(@NotNull DocumentEvent e)
            {
                Document document = e.getDocument();
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);

                if (file == null || file.getFileType() != OxyTemplateFileType.INSTANCE)
                {
                    return;
                }

                updateQueue.cancelAllUpdates();
                updateQueue.queue(new CompiledPreviewUpdater(Boolean.TRUE, myProject));
            }
        };

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(documentUpdateListener, myProject);
    }

    @Override
    public void dispose()
    {
        EditorFactory.getInstance().getEventMulticaster().removeDocumentListener(documentUpdateListener);

        updateQueue.dispose();
    }

    public void initToolWindow(@NotNull ToolWindow consoleWindow)
    {
        this.consoleWindow = consoleWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        JComponent consoleComponent = console.getComponent();
        Content content = contentFactory.createContent(consoleComponent, "", false);

        consoleWindow.getContentManager().addContent(content);
    }

    public boolean showCompiledCode(@NotNull final PsiFile originalFile)
    {
        PsiFile psiFile;
        Document document = null;
        VirtualFile virtualFile = null;
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

            if (psiFile != null && originalFile.isEquivalentTo(psiFile.getUserData(ORIGINAL_FILE_KEY)))
            {
                document = editor.getDocument();

                // find editor window where the file is opened
                for (EditorWindow window : fileEditorManager.getWindows())
                {
                    if (findFileIndex(window, virtualFile) != -1)
                    {
                        editorWindow = window;

                        break;
                    }
                }

                break;
            }
        }

        // create new editor
        if (document == null)
        {
            virtualFile = new LightVirtualFile(originalFile.getName() + COMPILED_FILE_SUFFIX);

            ((LightVirtualFile) virtualFile).setLanguage(CompiledPreview.INSTANCE);
            ((LightVirtualFile) virtualFile).setFileType(CompiledPreviewFileType.INSTANCE);

            psiFile = PsiManager.getInstance(myProject).findFile(virtualFile);

            assert psiFile != null;

            document = PsiDocumentManager.getInstance(myProject).getDocument(psiFile);

            assert document != null;

            psiFile.putUserData(ORIGINAL_FILE_KEY, originalFile);

            editorWindow = fileEditorManager.getCurrentWindow();
            newTabCreated = true;
        }

        assert editorWindow != null;

        if (result = showCompiledCode(originalFile, document))
        {
            if (newTabCreated)
            {
                editorWindow.split(SwingConstants.HORIZONTAL, false, virtualFile, false);
            }
            if (findFileIndex(editorWindow, virtualFile) == -1)
            {
                fileEditorManager.openFile(virtualFile, false);
            }

            // switch to editor with recompiled source
            EditorWithProviderComposite editor = editorWindow.findFileComposite(virtualFile);

            if (editor != null)
            {
                editorWindow.setEditor(editor, false);
            }
        }

        return result;
    }

    public boolean showCompiledCode(@NotNull final PsiFile originalFile, @NotNull final Document document)
    {
        long startTime = System.currentTimeMillis();

        try
        {
            CharSequence source = CompiledPreviewUtil.buildCompiledCode(originalFile);

            CommandProcessor.getInstance().executeCommand(myProject, () -> ApplicationManager.getApplication().runWriteAction(() ->
                    document.setText(source)
            ), I18nSupport.message("action.live.update"), "oxy-compiled-template-live-update-" + System.nanoTime());
        }
        catch (TemplateCompilerException e)
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

    private void onCompilerError(@NotNull final TemplateCompilerException exception, @NotNull final VirtualFile virtualFile)
    {
        ErrorInformation errorInformation;

        if (consoleWindow == null || ! consoleWindow.isVisible())
        {
            compilerNotificationGroup.createNotification(I18nSupport.message("action.live.update.compilation.error.message"),
                    MessageType.ERROR).notify(myProject);
        }

        console.print(I18nSupport.message("action.live.update.compilation.error", virtualFile.getPath()) + ":\n",
                ConsoleViewContentType.NORMAL_OUTPUT);

        if ((errorInformation = exception.getErrorInformation()) != null)
        {
            console.print(errorInformation.getMessage() + " - ", ConsoleViewContentType.ERROR_OUTPUT);
            console.print(I18nSupport.message("action.live.update.compilation.error.description", errorInformation.getFileName(),
                    errorInformation.getLineNumber()) + ":\n", ConsoleViewContentType.ERROR_OUTPUT);

            for (int i = 0; i < errorInformation.getSurrounding().size(); i++)
            {
                boolean highlightLine = errorInformation.getSurroundingStart() + i == errorInformation.getLineNumber();

                console.print((highlightLine ? "-->" : "") + errorInformation.getSurrounding().get(i) + '\n',
                        ConsoleViewContentType.ERROR_OUTPUT);
            }
        }
        else
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            exception.printStackTrace(printWriter);
            console.print(stringWriter.toString(), ConsoleViewContentType.ERROR_OUTPUT);

            printWriter.close();
        }
    }

    /**
     * @param editorWindow
     * @param virtualFile
     * @return index of virtualFile opened in given editorWindow, -1 if not found
     */
    private int findFileIndex(EditorWindow editorWindow, VirtualFile virtualFile)
    {
        int index = 0;

        for (VirtualFile file : editorWindow.getFiles())
        {
            if (file.equals(virtualFile)) {
                return index;
            }

            index++;
        }

        return -1;
    }

}
