package com.nexal.app.ui.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.ui.theme.ThemeState
import com.nexal.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    // Display
    val name: String = "",
    val email: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val age: Int = 0,
    val gender: String = "male",
    val targetWeight: Double = 0.0,
    val activityLevel: String = "moderately_active",
    val fitnessGoals: List<String> = listOf("general_fitness"),
    val intervalWeeks: Int = 6,
    val gymDaysPerWeek: Int = 5,
    val liftingExperience: String = "BEGINNER",
    val trainingLocation: String = "GYM",
    val unitSystem: String = "METRIC",
    val isDarkMode: Boolean = ThemeState.isDarkMode.value ?: false,
    // Edit form
    val editing: Boolean = false,
    val formName: String = "",
    val formWeight: String = "",
    val formHeight: String = "",
    val formAge: String = "",
    val formGender: String = "male",
    val formTargetWeight: String = "",
    val formActivityLevel: String = "moderately_active",
    val formGoals: List<String> = listOf("general_fitness"),
    val formIntervalWeeks: Int = 6,
    val formGymDays: Int = 5,
    val formLiftingExperience: String = "BEGINNER",
    val formTrainingLocation: String = "GYM",
    val formUnitSystem: String = "METRIC",
    // Status
    val saved: Boolean = false,
    val passwordLoading: Boolean = false,
    val passwordError: String? = null,
    val passwordChanged: Boolean = false,
    val emailLoading: Boolean = false,
    val emailError: String? = null,
    val emailSent: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadEmail()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepo.observeProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            name = profile.name,
                            weight = profile.weight,
                            height = profile.height,
                            age = profile.age,
                            gender = profile.gender.name.lowercase(),
                            targetWeight = profile.targetWeight,
                            activityLevel = profile.activityLevel.name.lowercase(),
                            fitnessGoals = profile.fitnessGoals.map { g -> g.name.lowercase() },
                            intervalWeeks = profile.intervalWeeks,
                            gymDaysPerWeek = profile.gymDaysPerWeek,
                            liftingExperience = profile.liftingExperience.name,
                            trainingLocation = profile.trainingLocation.name,
                            unitSystem = profile.unitSystem.name,
                            isDarkMode = ThemeState.isDarkMode.value ?: false
                        )
                    }
                }
            }
        }
    }

    private fun loadEmail() {
        viewModelScope.launch {
            authRepo.authState.collect { state ->
                if (state is com.nexal.app.domain.model.AuthState.Authenticated) {
                    _uiState.update { it.copy(email = state.email) }
                }
            }
        }
    }

    fun startEditing() {
        val s = _uiState.value
        _uiState.update {
            it.copy(
                editing = true,
                saved = false,
                formName = s.name,
                formWeight = s.weight.toString(),
                formHeight = s.height.toString(),
                formAge = s.age.toString(),
                formGender = s.gender,
                formTargetWeight = s.targetWeight.toString(),
                formActivityLevel = s.activityLevel,
                formGoals = s.fitnessGoals,
                formIntervalWeeks = s.intervalWeeks,
                formGymDays = s.gymDaysPerWeek,
                formLiftingExperience = s.liftingExperience,
                formTrainingLocation = s.trainingLocation,
                formUnitSystem = s.unitSystem
            )
        }
    }

    fun updateFormField(field: String, value: String) {
        _uiState.update {
            when (field) {
                "name" -> it.copy(formName = value)
                "weight" -> it.copy(formWeight = value)
                "height" -> it.copy(formHeight = value)
                "age" -> it.copy(formAge = value)
                "gender" -> it.copy(formGender = value)
                "targetWeight" -> it.copy(formTargetWeight = value)
                "activityLevel" -> it.copy(formActivityLevel = value)
                "intervalWeeks" -> it.copy(formIntervalWeeks = value.toIntOrNull() ?: 6)
                "gymDays" -> it.copy(formGymDays = value.toIntOrNull() ?: 5)
                "liftingExperience" -> it.copy(formLiftingExperience = value)
                "trainingLocation" -> it.copy(formTrainingLocation = value)
                "unitSystem" -> it.copy(formUnitSystem = value)
                else -> it
            }
        }
    }

    fun toggleGoal(goal: String) {
        val current = _uiState.value.formGoals
        val updated = if (goal in current) {
            if (current.size > 1) current - goal else current
        } else {
            current + goal
        }
        _uiState.update { it.copy(formGoals = updated) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val s = _uiState.value
            val current = profileRepo.getProfile() ?: return@launch
            val updated = current.copy(
                name = s.formName.trim(),
                weight = s.formWeight.toDoubleOrNull() ?: current.weight,
                height = s.formHeight.toDoubleOrNull() ?: current.height,
                age = s.formAge.toIntOrNull() ?: current.age,
                gender = com.nexal.app.domain.model.Gender.entries.find { it.name.lowercase() == s.formGender } ?: current.gender,
                activityLevel = com.nexal.app.domain.model.ActivityLevel.entries.find { it.name.lowercase() == s.formActivityLevel } ?: current.activityLevel,
                fitnessGoals = s.formGoals.mapNotNull { g -> com.nexal.app.domain.model.FitnessGoal.entries.find { it.name.lowercase() == g } },
                targetWeight = s.formTargetWeight.toDoubleOrNull() ?: current.targetWeight,
                intervalWeeks = s.formIntervalWeeks,
                gymDaysPerWeek = s.formGymDays,
                liftingExperience = runCatching { com.nexal.app.domain.model.LiftingExperience.valueOf(s.formLiftingExperience) }.getOrDefault(current.liftingExperience),
                trainingLocation = runCatching { com.nexal.app.domain.model.TrainingLocation.valueOf(s.formTrainingLocation) }.getOrDefault(current.trainingLocation),
                unitSystem = runCatching { com.nexal.app.domain.model.UnitSystem.valueOf(s.formUnitSystem) }.getOrDefault(current.unitSystem)
            )
            profileRepo.saveProfile(updated)
            _uiState.update { it.copy(editing = false, saved = true) }
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(saved = false) }
        }
    }

    fun toggleTheme() {
        ThemeState.toggle(application)
        _uiState.update { it.copy(isDarkMode = ThemeState.isDarkMode.value ?: false) }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(passwordLoading = true, passwordError = null) }
            when (val result = authRepo.changePassword(currentPassword, newPassword)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(passwordLoading = false, passwordChanged = true) }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(passwordChanged = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(passwordLoading = false, passwordError = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun changeEmail(newEmail: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(emailLoading = true, emailError = null) }
            when (val result = authRepo.changeEmail(newEmail)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(emailLoading = false, emailSent = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(emailLoading = false, emailError = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepo.logout()
        }
    }
}
