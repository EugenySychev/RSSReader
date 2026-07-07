package com.sychev.rss_reader.screens.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.sychev.domain.repositories.SourceRepository
import com.sychev.rss_reader.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainScreenViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()
    private var navController: NavController? = null

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            sourceRepository.getSources().collect { sources ->
                _uiState.update { state ->
                    state.copy(
                        sourceList = sources,
                    )
                }
            }
        }
    }

    fun onAction(action: MainScreenActions) {
        when(action) {
            is MainScreenActions.SelectNewsSource -> { }

            MainScreenActions.SelectSettings -> { }

            MainScreenActions.SelectSourcesScreen -> { }

            is MainScreenActions.OnNavigateFromMenu -> {
                if (action.url.isNotBlank()) {
                    navController?.navigate(Destination.NewsSource.createRoute(action.url))
                } else {
                    navController?.navigate(action.route) {
                        navController?.graph?.startDestinationId?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }

    fun setNavController(navController: NavHostController) {
        this.navController = navController
    }
}