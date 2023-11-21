package ilapin.networkaccesscontrol.data

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import ilapin.networkaccesscontrol.domain.AppWithNetworkPermission
import ilapin.networkaccesscontrol.domain.AppsRepository
import ilapin.networkaccesscontrol.domain.RestrictedNetworkAccessPackageNamesRepository
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