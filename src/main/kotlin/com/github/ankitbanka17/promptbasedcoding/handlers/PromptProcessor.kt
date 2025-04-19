package com.github.ankitbanka17.promptbasedcoding.handlers

import com.github.ankitbanka17.promptbasedcoding.handlers.adapter.LLMAdapter
import com.github.ankitbanka17.promptbasedcoding.services.CodeModifier
import com.intellij.openapi.project.Project

class PromptProcessor(private val project: Project) {
    private val llmAdapter = LLMAdapter()
    private val codeModifier = CodeModifier()
    private val fileHandler = FileHandler()
    private val documentWriter = DocumentWriter()

    fun process(prompt: String, onSuccess: (String) -> Unit) {
        try {
            val metaData = llmAdapter.extractCodeInstructions(prompt)
                ?: return Notifier.showError(project, "Failed to retrieve metadata.")

            val modifiedCodeArray = codeModifier.modifyCode(prompt, metaData)
            val modifiedCode = modifiedCodeArray?.getOrNull(1)
                ?: return Notifier.showError(project, "Failed to modify code.")

            val isUpdate = metaData["action"] != "create_class"
            val className = metaData["className"].toString()

            val virtualFile = fileHandler.getFile(className, isUpdate)
                ?: return Notifier.showError(project, "Could not find or create file.")

            documentWriter.write(project, virtualFile, modifiedCode)
            onSuccess("Code modified successfully!")

        } catch (ex: Exception) {
            Notifier.showError(project, "Error: ${ex.message}")
        }
    }
}
