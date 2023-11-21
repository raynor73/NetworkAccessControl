package ilapin.networkaccesscontrol2.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ilapin.networkaccesscontrol2.data.AndroidAppsRepository
import ilapin.networkaccesscontrol2.data.FsRestrictedNetworkAccessPackageNamesRepository
import ilapin.networkaccesscontrol2.domain.AppsRepository
import ilapin.networkaccesscontrol2.domain.RestrictedNetworkAccessPackageNamesRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindAppsRepository(androidAppsRepository: AndroidAppsRepository): AppsRepository

    @Binds
    abstract fun bindRestrictedNetworkAccessPackageNamesRepository(fsRestrictedNetworkAccessPackageNamesRepository: FsRestrictedNetworkAccessPackageNamesRepository): RestrictedNetworkAccessPackageNamesRepository
}