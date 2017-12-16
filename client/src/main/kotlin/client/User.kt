package client

data class User(
    val login: String,
    val fullName: String,
    val company: String = "",
    val createdAt: String,
    val email: String,
    val avatarUrl: String = "",
    val profileUrl: String,
    val publicRepos: Int = 0
)