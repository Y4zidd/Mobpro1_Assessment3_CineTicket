package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.model

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val email: String?
)

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
