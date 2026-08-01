package com.nexal.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.data.repository.WorkoutRepository
import com.nexal.app.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository,
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    init {
        // After login / DB wipe, restore profile + plans so AI screens aren't empty.
        viewModelScope.launch {
            authRepository.authState
                .map { it is AuthState.Authenticated }
                .distinctUntilChanged()
                .collect { authenticated ->
                    if (authenticated) {
                        profileRepository.syncFromServer()
                        workoutRepository.syncFromServer()
                        nutritionRepository.syncFromServer()
                    }
                }
        }
    }
}
