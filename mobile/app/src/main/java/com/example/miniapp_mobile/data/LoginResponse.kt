package data

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String?
)