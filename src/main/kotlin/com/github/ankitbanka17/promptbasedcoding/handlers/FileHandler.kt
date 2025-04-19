package com.github.ankitbanka17.promptbasedcoding.handlers

import com.github.ankitbanka17.promptbasedcoding.services.GlobalCodeMetaData
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class FileHandler {
    private val basePath = "/Users/ankitbanka/Downloads/demo/src/main/java/com/example/demo/"

    fun getFile(className: String, isUpdate: Boolean): VirtualFile? {
        return if (isUpdate) {
            val existing = GlobalCodeMetaData.embeddings.find { it.fileName == "$className.java" }
            existing?.filePath?.let { LocalFileSystem.getInstance().findFileByPath(it) }
        } else {
            val fullPath = "$basePath$className.java"
            File(fullPath).apply { createNewFile() }
            LocalFileSystem.getInstance().refreshAndFindFileByPath(fullPath)
        }
    }
}
