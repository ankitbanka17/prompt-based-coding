package com.github.ankitbanka17.promptbasedcoding.util

import java.nio.file.Files
import java.nio.file.Paths

object PromptBuilder {

    private val promptTemplate: String by lazy {
        val resource = this::class.java.classLoader.getResource("prompts/code_instructions.txt")
            ?: error("Prompt file not found.")
        resource.readText()
    }

    fun buildCodeInstructionPrompt(existingClasses: String): String {
        return promptTemplate.replace("{{existingClasses}}", existingClasses)
    }
}
