@file:OptIn(ExperimentalMaterialApi::class)

package ilapin.networkaccesscontrol2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Snackbar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ilapin.networkaccesscontrol2.domain.AppWithNetworkPermission
import ilapin.networkaccesscontrol2.ui.theme.NetworkAccessControlTheme
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainScreenViewModel by viewModels()
        setContent {
            NetworkAccessControlTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
private fun MainScreen(viewModel: MainScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SideEffect {
        viewModel.onViewShown()
    }
    MainScreen(
        state = state,
        onCheckboxClick = { viewModel.onAppClicked(it) },
        onRefresh = { viewModel.refresh() },
        onDismissError = { viewModel.dismissError() }
    )
}

@Composable
private fun MainScreen(
    state: MainScreenViewModel.State,
    onCheckboxClick: (AppWithNetworkPermission) -> Unit,
    onRefresh: () -> Unit,
    onDismissError: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (state) {
            MainScreenViewModel.State.IsLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is MainScreenViewModel.State.Loaded -> {
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = state.isRefreshing,
                    onRefresh = onRefresh
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(state.appsWithNetworkPermission) {app ->
                            AppItem(
                                app = app,
                                onCheckboxClick = { onCheckboxClick(app) },
                                isEnabled = state.isInteractionEnabled
                            )
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = state.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    state.errorMessage?.let { errorMessage ->
                        Snackbar(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.BottomCenter),
                            action = {
                                Button(
                                    onClick = onDismissError
                                ) {
                                    Text(stringResource(R.string.dismiss))
                                }
                            }
                        ) {
                            Text(errorMessage)
                        }
                        LaunchedEffect(state.errorMessage) {
                            delay(3000)
                            onDismissError()
                        }
                    }
                }
            }

            MainScreenViewModel.State.NotLoaded -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.please_wait)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppWithNetworkPermission,
    onCheckboxClick: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(app.packageName)
        Checkbox(
            checked = !app.isNetworkAccessRestricted,
            onCheckedChange = { onCheckboxClick() },
            enabled = isEnabled
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    NetworkAccessControlTheme {
        MainScreen(
            state = MainScreenViewModel.State.Loaded(
                appsWithNetworkPermission = listOf(
                    AppWithNetworkPermission(
                        packageName = "com.example.one",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.two",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.three",
                        isNetworkAccessRestricted = true
                    ),
                ),
                isInteractionEnabled = true,
                isRefreshing = false,
                errorMessage = null
            ),
            onCheckboxClick = {},
            onRefresh = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenErrorPreview() {
    NetworkAccessControlTheme {
        MainScreen(
            state = MainScreenViewModel.State.Loaded(
                appsWithNetworkPermission = listOf(
                    AppWithNetworkPermission(
                        packageName = "com.example.one",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.two",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.three",
                        isNetworkAccessRestricted = true
                    ),
                ),
                isInteractionEnabled = true,
                isRefreshing = true,
                errorMessage = "IOException"
            ),
            onCheckboxClick = {},
            onRefresh = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenRefreshingPreview() {
    NetworkAccessControlTheme {
        MainScreen(
            state = MainScreenViewModel.State.Loaded(
                appsWithNetworkPermission = listOf(
                    AppWithNetworkPermission(
                        packageName = "com.example.one",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.two",
                        isNetworkAccessRestricted = false
                    ),
                    AppWithNetworkPermission(
                        packageName = "com.example.three",
                        isNetworkAccessRestricted = true
                    ),
                ),
                isInteractionEnabled = true,
                isRefreshing = true,
                errorMessage = null
            ),
            onCheckboxClick = {},
            onRefresh = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenLoadingPreview() {
    NetworkAccessControlTheme {
        MainScreen(
            state = MainScreenViewModel.State.IsLoading,
            onCheckboxClick = {},
            onRefresh = {},
            onDismissError = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenNotLoadedPreview() {
    NetworkAccessControlTheme {
        MainScreen(
            state = MainScreenViewModel.State.NotLoaded,
            onCheckboxClick = {},
            onRefresh = {},
            onDismissError = {},
        )
    }
}