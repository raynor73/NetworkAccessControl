package ilapin.networkaccesscontrol2.domain

interface RestrictedNetworkAccessPackageNamesRepository {
    suspend fun getPackageNames(): List<String>
    suspend fun addPackageName(packageName: String)
    suspend fun removePackageName(packageName: String)
}