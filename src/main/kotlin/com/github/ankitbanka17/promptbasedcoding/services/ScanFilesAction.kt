package com.github.ankitbanka17.promptbasedcoding.services


import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.project.Project

/**
 * This action scans all project files, extracts their code, generates embeddings,
 * and stores them in the vector (in-memory) database.
 */
public class ScanFilesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        // Create an instance of the embedder.

        // Create the ingestor with current project and embedder.
        val ingestor = CodeIngestor(project)
        // Scan the project and ingest the files.
        ingestor.scanProjectFilesAndIngest()
        Messages.showInfoMessage(project, "Code ingestion completed.", "Success")
    }
}
