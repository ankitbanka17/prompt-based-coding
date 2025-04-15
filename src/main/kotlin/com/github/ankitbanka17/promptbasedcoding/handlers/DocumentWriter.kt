package com.github.ankitbanka17.promptbasedcoding.handlers

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class DocumentWriter {
    fun write(project: Project, virtualFile: VirtualFile, content: String) {
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
        if (document == null) {
            Notifier.showError(project, "Could not obtain document.")
            return
        }

        ApplicationManager.getApplication().invokeAndWait {
            WriteCommandAction.runWriteCommandAction(project) {
                document.setText(content)
            }
        }
    }
}
