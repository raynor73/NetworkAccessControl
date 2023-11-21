package ilapin.networkaccesscontrol2.domain

interface AppsRepository {
    suspend fun getAppsWithNetworkPermission(): List<AppWithNetworkPermission>
}