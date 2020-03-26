package com.example.repokoltin.view.login

interface LoginView {
    fun onLoginFail()
    fun onLoginSuccess()
    fun onPasswordNotValid()
    fun autoLogin()
}