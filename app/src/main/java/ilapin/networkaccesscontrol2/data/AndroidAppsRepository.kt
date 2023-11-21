package ilapin.networkaccesscontrol2.data

import android.Manifest
import android.content.pm.PackageManager
import ilapin.networkaccesscontrol2.domain.AppWithNetworkPermission
import ilapin.networkaccesscontrol2.domain.AppsRepository
import ilapin.networkaccesscontrol2.domain.RestrictedNetworkAccessPackageNamesRepository
import javax.inject.Inject

class AndroidAppsRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val restrictedNetworkAccessPackageNamesRepository: RestrictedNetworkAccessPackageNamesRepository
) : AppsRepository {

    override suspend fun getAppsWithNetworkPermission(): List<AppWithNetworkPermission> {
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val restrictedNetworkAccessPackageNames = restrictedNetworkAccessPackageNamesRepository.getPackageNames()

        return packages.mapNotNull { applicationInfo ->
            val packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)
            if (packageInfo?.requestedPermissions?.contains(Manifest.permission.INTERNET) == true) {
                AppWithNetworkPermission(
                    applicationInfo.packageName,
                    restrictedNetworkAccessPackageNames.contains(applicationInfo.packageName)
                )
            } else {
                null
            }
        }
    }
}