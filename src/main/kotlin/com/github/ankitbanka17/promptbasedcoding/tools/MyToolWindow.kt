package com.github.ankitbanka17.promptbasedcoding.tools

import com.github.ankitbanka17.promptbasedcoding.handlers.adapter.LLMAdapter
import com.github.ankitbanka17.promptbasedcoding.services.CodeModifier
import com.github.ankitbanka17.promptbasedcoding.services.GlobalCodeMetaData
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.awt.*
import java.io.File
import javax.swing.*

class MyToolWindow(private val project: Project) {
    companion object {
        private const val CLASS_FILE_DIR = "/Users/ankitbanka/Downloads/demo/src/main/java/com/example/demo/"
    }

    private val contentPanel = JPanel(BorderLayout())
    private val inputField = JTextField()
    private val runButton = JButton("Run")
    private val resultArea = JTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
    }

    init {
        setupUI()
        setupListeners()
    }

    fun getContent(): JPanel = contentPanel

    private fun setupUI() {
        val inputLabel = JLabel("Prompt:").apply {
            preferredSize = Dimension(60, 200)
        }

        inputField.preferredSize = Dimension(400, 200)

        val topPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(inputLabel)
            add(Box.createHorizontalStrut(10))
            add(inputField)
            add(Box.createHorizontalStrut(10))
            add(runButton)
        }

        val scrollPane = JScrollPane(resultArea).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        contentPanel.add(topPanel, BorderLayout.NORTH)
        contentPanel.add(scrollPane, BorderLayout.CENTER)
    }

    private fun setupListeners() {
        runButton.addActionListener {
            val input = inputField.text

            ApplicationManager.getApplication().invokeLater {
                ProgressManager.getInstance().run(object : Task.Modal(project, "Processing Prompt", true) {
                    override fun run(indicator: ProgressIndicator) {
                        processPrompt(input, indicator)
                    }
                })
            }
        }
    }

    private fun processPrompt(input: String, indicator: ProgressIndicator) {
        try {
            indicator.text = "Extracting metadata from prompt..."
            val metaData = extractMetadata(input) ?: return

            val newClasses = metaData["newClasses"] as? List<*> ?: emptyList<Any>()
            val modifiedClasses = metaData["modifiedClasses"] as? List<*> ?: emptyList<Any>()
            val allTargetClasses = (newClasses + modifiedClasses).filterIsInstance<String>()

            val total = allTargetClasses.size
            allTargetClasses.forEachIndexed { index, className ->
                indicator.text = "Processing class: $className (${index + 1}/$total)"
                indicator.fraction = (index + 1).toDouble() / total
                modifyCode(input, metaData + mapOf("className" to className))
            }

            indicator.text = "Finalizing..."
            showSuccess(input)

        } catch (ex: Exception) {
            showError("Error: ${ex.message}")
        }
    }


    private fun modifyCode(input: String, metaData: Map<String, Any?>) {
        val modifiedCode = getModifiedCode(input, metaData) ?: return
        val virtualFile = getOrCreateTargetFile(metaData) ?: return
        var document : Document? = null
        ApplicationManager.getApplication().runReadAction {
             document = FileDocumentManager.getInstance().getDocument(virtualFile)
                ?: return@runReadAction showError("Could not obtain document.")
        }

        document?.let { writeModifiedCodeToFile(it, modifiedCode) }
    }

    private fun extractMetadata(input: String): Map<String, Any?>? {
        val llmAdapter = LLMAdapter()
        val metaData = llmAdapter.extractCodeInstructions(input)
        if (metaData == null) {
            showError("Failed to retrieve metadata.")
            return null
        }
        return metaData
    }

    private fun getModifiedCode(input: String, metaData: Map<String, Any?>): String? {
        val codeModifier = CodeModifier()
        val modifiedCodeArray = codeModifier.modifyCode(input, metaData)
        val modifiedCode = modifiedCodeArray?.getOrNull(1)

        if (modifiedCode == null) {
            showError("Failed to modify code.")
            return null
        }
        return modifiedCode
    }

    private fun getOrCreateTargetFile(metaData: Map<String, Any?>): VirtualFile? {
        val isUpdate = metaData["action"] != "create_class"
        val className = metaData["className"].toString()
        return getTargetFile(isUpdate, className) ?: run {
            showError("File not found or could not be created.")
            null
        }
    }

    private fun writeModifiedCodeToFile(document: Document, modifiedCode: String) {
        ApplicationManager.getApplication().invokeAndWait ({
            WriteCommandAction.runWriteCommandAction(project) {
                document.setText(modifiedCode)
            }
        }, ModalityState.defaultModalityState())
    }

    private fun showSuccess(input: String) {
        SwingUtilities.invokeLater {
            resultArea.text = "You entered: $input"
            Messages.showMessageDialog(
                project,
                "Code modified successfully!",
                "Success",
                Messages.getInformationIcon()
            )
        }
    }



    private fun getTargetFile(isUpdate: Boolean, className: String): VirtualFile? {
        return if (isUpdate) {
            val existingFile = GlobalCodeMetaData.embeddings.find { it.fileName == "$className.java" }
            existingFile?.filePath?.let { LocalFileSystem.getInstance().findFileByPath(it) }
        } else {
            val path = "$CLASS_FILE_DIR$className.java"
            val ioFile = File(path)
            if (!ioFile.exists()) ioFile.createNewFile()
            LocalFileSystem.getInstance().refreshAndFindFileByPath(path)
        }
    }

    private fun showError(message: String) {
        SwingUtilities.invokeLater {
            Messages.showMessageDialog(
                project,
                message,
                "Error",
                Messages.getErrorIcon()
            )
        }
    }

    private fun getActiveEditor(): Editor? {
        return FileEditorManager.getInstance(project).selectedTextEditor
    }
}
