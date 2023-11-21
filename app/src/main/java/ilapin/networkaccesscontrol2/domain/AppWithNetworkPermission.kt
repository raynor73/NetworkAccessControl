package ilapin.networkaccesscontrol2.domain

data class AppWithNetworkPermission(
    val packageName: String,
    val isNetworkAccessRestricted: Boolean
)
