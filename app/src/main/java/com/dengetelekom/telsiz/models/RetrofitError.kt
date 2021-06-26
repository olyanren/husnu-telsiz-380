package com.dengetelekom.telsiz.models

data class RetrofitError (

    val status: String,
    val message: String
)
data class RetrofitSuccess(

    val id: String,


    val name: String,


    val avatar: String
)
