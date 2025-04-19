package com.github.ankitbanka17.promptbasedcoding.services

// Data class for holding a code snippet and its associated embedding ID.
data class CodeEmbedding(val filePath: String, val code: String, val embeddingId: String,val embedding: List<Float>)
data class CodeMetaData(val fileName: String, val filePath: String,val code: String)

// A singleton object that acts as an in‑memory vector database.
object GlobalCodeEmbeddings {
    val embeddings: MutableList<CodeEmbedding> = mutableListOf()
}

object GlobalCodeMetaData {
    val embeddings: MutableList<CodeMetaData> = mutableListOf()
}

