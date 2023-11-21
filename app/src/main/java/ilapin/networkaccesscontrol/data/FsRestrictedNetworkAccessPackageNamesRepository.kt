package ilapin.networkaccesscontrol.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ilapin.networkaccesscontrol.domain.RestrictedNetworkAccessPackageNamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FsRestrictedNetworkAccessPackageNamesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : RestrictedNetworkAccessPackageNamesRepository {

    override suspend fun getPackageNames(): List<String> {
        val file = File(context.filesDir, RESTRICTED_NETWORK_ACCESS_PACKAGE_NAMES_LIST_FILENAME)
        return if (!file.exists()) {
            emptyList()
        } else {
            withContext(Dispatchers.IO) {
                val packageNames = mutableListOf<String>()
                val reader = file.bufferedReader()
                var line = reader.readLine()?.trim()
                while (line != null) {
                    if (line.isNotBlank()) {
                        packageNames += line
                    }

                    line = reader.readLine()?.trim()
                }
                reader.close()
                packageNames.distinct()
            }
        }
    }

    override suspend fun addPackageName(packageName: String) {
        val file = File(context.filesDir, RESTRICTED_NETWORK_ACCESS_PACKAGE_NAMES_LIST_FILENAME)
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }

            savePackageNames(file, listOf(packageName))
        } else {
            val packageNames = getPackageNames().toMutableList()
            packageNames += packageName

            savePackageNames(file, packageNames)
        }
    }

    override suspend fun removePackageName(packageName: String) {
        val file = File(context.filesDir, RESTRICTED_NETWORK_ACCESS_PACKAGE_NAMES_LIST_FILENAME)
        if (!file.exists()) {
            return
        }

        val packageNames = getPackageNames().toMutableList()
        packageNames -= packageName

        savePackageNames(file, packageNames)
    }

    private suspend fun savePackageNames(file: File, packageNames: List<String>) {
        withContext(Dispatchers.IO) {
            val writer = file.bufferedWriter()
            packageNames.forEach {
                writer.write(it)
                writer.newLine()
            }
            writer.close()
        }
    }

    companion object {

        private const val RESTRICTED_NETWORK_ACCESS_PACKAGE_NAMES_LIST_FILENAME = "restricted_network_access_package_names"
    }
}