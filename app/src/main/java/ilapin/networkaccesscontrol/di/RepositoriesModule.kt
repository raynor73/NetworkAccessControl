package ilapin.networkaccesscontrol.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ilapin.networkaccesscontrol.data.AndroidAppsRepository
import ilapin.networkaccesscontrol.data.FsRestrictedNetworkAccessPackageNamesRepository
import ilapin.networkaccesscontrol.domain.AppsRepository
import ilapin.networkaccesscontrol.domain.RestrictedNetworkAccessPackageNamesRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindAppsRepository(androidAppsRepository: AndroidAppsRepository): AppsRepository

    @Binds
    abstract fun bindRestrictedNetworkAccessPackageNamesRepository(fsRestrictedNetworkAccessPackageNamesRepository: FsRestrictedNetworkAccessPackageNamesRepository): RestrictedNetworkAccessPackageNamesRepository
}