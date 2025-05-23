package com.github.ankitbanka17.promptbasedcoding.handlers.adapter

import com.github.ankitbanka17.promptbasedcoding.services.GlobalCodeMetaData
import com.github.ankitbanka17.promptbasedcoding.util.PromptBuilder
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class LLMAdapter(
    private val apiKey: String= "",
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) {

    fun getAllFileNames(): String {
        return GlobalCodeMetaData.embeddings.joinToString(", ") { it.fileName }
    }

    fun extractCodeInstructions(userInput: String): Map<String, Any?> {
        val systemPrompt = PromptBuilder.buildCodeInstructionPrompt(getAllFileNames())
        val requestBody = mapOf(
            "model" to "gpt-4o-mini",
            "response_format" to mapOf("type" to "json_object"),
            "messages" to listOf(
                mapOf("role" to "system", "content" to systemPrompt),
                mapOf("role" to "user", "content" to userInput)
            )
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(RequestBody.create("application/json".toMediaType(), gson.toJson(requestBody)))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: error("No response from OpenAI")
            val json = JSONObject(body)
            val content = json.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?: "{}"

            return parseResponseContent(content)
        }
    }
    fun retrieveModifiedCode(
        systemPrompt: String,
        userPrompt: String,
        classRequired: String
    ): Array<String>? {
        val json = JSONObject().apply {
            put("model", "gpt-4o")
            put("response_format", JSONObject().put("type", "json_object"))
            put("temperature", 0.3)
            put(
                "messages", listOf(
                    JSONObject().put("role", "system").put("content", systemPrompt),
                    JSONObject().put("role", "user").put(
                        "content",
                        userPrompt + "\n In this step provide code for only this class: " + classRequired
                    )
                )
            )
        }

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return null
            val choices = JSONObject(responseBody).getJSONArray("choices")
            if (choices.length() > 0) {
                val messageContent = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // Parse the JSON response returned by the AI
                val responseJson = JSONObject(messageContent)
                val fileName = responseJson.optString("fileName", "DefaultFileName.kt")
                val codeContent = responseJson.optString("code", "")

                arrayOf(fileName, codeContent)
            } else null
        } else {
            println("Error modifying code: ${response.message}")
            null
        }
    }

    private fun parseResponseContent(content: String): Map<String, Any?> {
        val jsonObject = runCatching { JSONObject(content) }.getOrNull() ?: return defaultResponse()

        val action = jsonObject.optString("action", "default_action")

        val newClasses = jsonObject.optJSONArray("newClasses")?.let { array ->
            List(array.length()) { index -> array.optString(index) }
        } ?: emptyList<String>()

        val modifiedClasses = jsonObject.optJSONArray("modifiedClasses")?.let { array ->
            List(array.length()) { index -> array.optString(index) }
        } ?: emptyList<String>()

        val referencedClasses = jsonObject.optJSONArray("referencedClasses")?.let { array ->
            List(array.length()) { index -> array.optString(index) }
        } ?: emptyList<String>()

        val additionalNotes = jsonObject.optString("additionalNotes", "No additional notes provided")

        return mapOf(
            "action" to action,
            "newClasses" to newClasses,
            "modifiedClasses" to modifiedClasses,
            "referencedClasses" to referencedClasses,
            "additionalNotes" to additionalNotes
        )
    }

    private fun defaultResponse() = mapOf(
        "action" to "default_action",
        "className" to "default_class",
        "referencedClasses" to emptyList<String>(),
        "additionalNotes" to "No additional notes provided"
    )
}
