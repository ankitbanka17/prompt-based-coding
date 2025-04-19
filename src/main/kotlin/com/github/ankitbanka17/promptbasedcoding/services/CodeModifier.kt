package com.github.ankitbanka17.promptbasedcoding.services

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CodeModifier {
    // Retrieve the API key from the plugin settings.
    private val apiKey=""

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) // time to establish the connection
        .readTimeout(30, TimeUnit.SECONDS)    // time to wait for the server's response
        .writeTimeout(15, TimeUnit.SECONDS)   // time to send the request
        .build()

    /**
     * Calls the OpenAI chat completions endpoint with the original code and modification prompt.
     * Returns the modified code as a string.
     */
    fun modifyCode(originalCode: String?, prompt: String): String? {
        client.callTimeoutMillis
        val systemPrompt = "You are an expert java spring boot programmer. Modify the following code based on the instruction provided. If original code is not present , create a new code"
        var userPrompt=""
        if(originalCode == null){
             userPrompt = "Original Code: Not Present \n\nModification: $prompt"
        }else{
             userPrompt = "Original Code: \n$originalCode \n\nModification: $prompt"
        }

        val json = JSONObject().apply {
            put("model", "gpt-4")
            put("temperature", 0.3)
            // Assemble the messages array as required by the Chat API
            put("messages", listOf(
                JSONObject().put("role", "system").put("content", systemPrompt),
                JSONObject().put("role", "user").put("content", userPrompt)
            ))
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
                choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } else null
        } else {
            println("Error modifying code: ${response.message}")
            null
        }
    }

    fun modifyCode(prompt: String, metaData: Map<String,Any?>): Array<String>? {
        var existinCode: String? = ""
        val classes = metaData["referencedClasses"] as List<String>
        val classRequired= metaData["className"] as String
        classes.forEach { referencedClass ->
            println("Referenced Class: $referencedClass")
            val codeMetaData=GlobalCodeMetaData.embeddings
                .filter { it.fileName == referencedClass+".java" }.first()
            existinCode+="ClassNAme:"+ codeMetaData.fileName +"Code: "+ codeMetaData.code

        }

        val systemPrompt = "You are an expert and strict spring boot java programmer. Provide a response in JSON format with 'fileName' and 'code' keys based on the instructions. Do not include explanations or comments."
        val userPrompt = "Original Code: $existinCode \n\nModification: $prompt"

        val json = JSONObject().apply {
            put("model", "gpt-4o")
            put("response_format", JSONObject().put("type", "json_object"))
            put("temperature", 0.3)
            put(
                "messages", listOf(
                    JSONObject().put("role", "system").put("content", systemPrompt),
                    JSONObject().put("role", "user").put("content", userPrompt + "\n In this step provide code for only this class: "+classRequired)
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
}
