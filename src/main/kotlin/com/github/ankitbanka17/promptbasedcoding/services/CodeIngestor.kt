package com.github.ankitbanka17.promptbasedcoding.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

class CodeIngestor(
    private val project: Project,

) {
    // Define supported file extensions.
    private val supportedExtensions = setOf("java", "kt", "py")

    /**
     * Starts scanning the project from the base directory.
     */
    fun scanProjectFilesAndIngest() {
        val baseDir = project.baseDir ?: return
        scanDirectoryAndIngest(baseDir)
    }

    /**
     * Recursively scans a directory for supported files.
     */
    private fun scanDirectoryAndIngest(directory: VirtualFile) {
        for (child in directory.children) {
            if (child.isDirectory) {
                scanDirectoryAndIngest(child)
            } else if (supportedExtensions.contains(child.extension)) {
                ingestFile(child)
            }
        }
    }

    /**
     * For each file, converts it to a PsiFile, extracts its code, and generates embeddings.
     * Then stores the file path, code, and embedding ID into the global vector store.
     */
    private fun ingestFile(file: VirtualFile) {
        val psiFile = PsiManager.getInstance(project).findFile(file) ?: return
        val codeContent = psiFile.text
        if (codeContent.isNotBlank()) {
           // val embedding = embedder.generateEmbeddingsForCode(codeContent)
            GlobalCodeMetaData.embeddings.add(CodeMetaData(file.name, file.path,codeContent))

                //GlobalCodeEmbeddings.embeddings.add(CodeEmbedding(file.path, embedding.code, embedding.embeddingId, embedding.embedding))
               // println("Ingested file: ${file.path} with embedding ID: ${embedding.embeddingId}")
            }
        }
    }

