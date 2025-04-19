package com.github.ankitbanka17.promptbasedcoding.services

import com.github.ankitbanka17.promptbasedcoding.handlers.adapter.LLMAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CodeModifier {
    // Retrieve the API key from the plugin settings.


    private val llmAdapter= LLMAdapter()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) // time to establish the connection
        .readTimeout(30, TimeUnit.SECONDS)    // time to wait for the server's response
        .writeTimeout(15, TimeUnit.SECONDS)   // time to send the request
        .build()




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

        return llmAdapter.retrieveModifiedCode(systemPrompt, userPrompt, classRequired)
    }


}
