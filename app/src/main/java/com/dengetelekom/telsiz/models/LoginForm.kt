package com.dengetelekom.telsiz.models

data class LoginForm(
        val username: String? =null,
        val password: String? =null,
        val rememberMe: Boolean=false

)