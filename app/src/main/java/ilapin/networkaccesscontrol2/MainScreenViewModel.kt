package ilapin.networkaccesscontrol2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ilapin.networkaccesscontrol2.domain.AppWithNetworkPermission
import ilapin.networkaccesscontrol2.domain.AppsRepository
import ilapin.networkaccesscontrol2.domain.RestrictedNetworkAccessPackageNamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val appsRepository: AppsRepository,
    private val restrictedNetworkAccessPackageNamesRepository: RestrictedNetworkAccessPackageNamesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.NotLoaded)

    val state = _state.asStateFlow()

    fun onViewShown() {
        if (state.value == State.NotLoaded) {
            viewModelScope.launch {
                _state.emit(State.IsLoading)
                loadApps(emptyList())
            }
        }
    }

    fun refresh() {
        val state = state.value as? State.Loaded ?: return
        if (!state.isInteractionEnabled || state.isRefreshing) {
            return
        }

        viewModelScope.launch {
            _state.emit(state.copy(isRefreshing = true))
            loadApps(state.appsWithNetworkPermission)
        }
    }

    fun onAppClicked(app: AppWithNetworkPermission) {
        val state = state.value as? State.Loaded ?: return
        if (!state.isInteractionEnabled || state.isRefreshing) {
            return
        }

        viewModelScope.launch {
            _state.emit(state.copy(isInteractionEnabled = false))
            if (app.isNetworkAccessRestricted) {
                restrictedNetworkAccessPackageNamesRepository.removePackageName(app.packageName)
            } else {
                restrictedNetworkAccessPackageNamesRepository.addPackageName(app.packageName)
            }
            loadApps(state.appsWithNetworkPermission)
        }
    }

    fun dismissError() {
        val state = state.value as? State.Loaded ?: return
        viewModelScope.launch {
            _state.emit(state.copy(errorMessage = null))
        }
    }

    private suspend fun loadApps(fallbackApps: List<AppWithNetworkPermission>) {
        _state.emit(
            try {
                State.Loaded(
                    appsWithNetworkPermission = appsRepository.getAppsWithNetworkPermission(),
                    isInteractionEnabled = true,
                    isRefreshing = false,
                    errorMessage = null
                )
            } catch (t: Throwable) {
                State.Loaded(
                    appsWithNetworkPermission = fallbackApps,
                    isInteractionEnabled = true,
                    isRefreshing = false,
                    errorMessage = t.javaClass.simpleName
                )
            }
        )
    }

    sealed class State {
        object NotLoaded : State()

        object IsLoading : State()

        data class Loaded(
            val appsWithNetworkPermission: List<AppWithNetworkPermission>,
            val isInteractionEnabled: Boolean,
            val isRefreshing: Boolean,
            val errorMessage: String?
        ) : State()
    }
}