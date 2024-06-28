package com.example.synomprojectkotlin.models

data class Word(
    val id: Int,
    var name: String,
    var definition: String?,
    val synonyms: Map<String, String>?,
    val translate: String
)