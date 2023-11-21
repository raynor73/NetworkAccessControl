package ilapin.networkaccesscontrol.domain

data class AppWithNetworkPermission(
    val packageName: String,
    val isNetworkAccessRestricted: Boolean
)
