package ilapin.networkaccesscontrol.domain

interface AppsRepository {
    suspend fun getAppsWithNetworkPermission(): List<AppWithNetworkPermission>
}