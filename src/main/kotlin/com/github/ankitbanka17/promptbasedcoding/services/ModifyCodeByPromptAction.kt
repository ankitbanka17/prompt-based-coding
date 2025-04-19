package com.github.ankitbanka17.promptbasedcoding.services

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem

public class ModifyCodeByPromptAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Get the prompt from the user.
        val modificationPrompt = Messages.showInputDialog(project, "Enter code modification prompt:", "Modify Code", null)
            ?: return

        // For demo purposes, retrieve the first ingested code snippet.
        if (GlobalCodeEmbeddings.embeddings.isEmpty()) {
            Messages.showMessageDialog(project, "No ingested code available. Please run the scan action first.", "Error", Messages.getErrorIcon())
            return
        }
        val codeEmbedding = GlobalCodeEmbeddings.embeddings.first()
        val originalCode = codeEmbedding.code

        // Call the code modifier to modify the original code per the prompt.
        val codeModifier = CodeModifier()
        val modifiedCode = codeModifier.modifyCode(originalCode, modificationPrompt)
        if (modifiedCode == null) {
            Messages.showMessageDialog(project, "Failed to modify code.", "Error", Messages.getErrorIcon())
            return
        }

        // Find the file corresponding to the code snippet.
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(codeEmbedding.filePath)
        if (virtualFile == null) {
            Messages.showMessageDialog(project, "File not found: ${codeEmbedding.filePath}", "Error", Messages.getErrorIcon())
            return
        }

        // Get the document and replace its text. (For simplicity, replacing the entire file.)
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
        if (document != null) {
            com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction {
                document.setText(modifiedCode)
            }
            Messages.showMessageDialog(project, "Code modified successfully!", "Success", Messages.getInformationIcon())
        } else {
            Messages.showMessageDialog(project, "Could not obtain document for file.", "Error", Messages.getErrorIcon())
        }
    }
}
