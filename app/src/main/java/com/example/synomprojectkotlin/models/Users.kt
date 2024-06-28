package com.example.synomprojectkotlin.models

class Users(
    var id: Int,
    var username: String,
    var email: String,
    var password: String,
    var roleId: Int,
    val maxSynonymScore: Int = 0,
    val maxDefinitionScore: Int = 0
)