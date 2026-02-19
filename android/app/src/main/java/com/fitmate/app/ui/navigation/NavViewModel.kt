package com.fitmate.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.fitmate.app.data.repository.AuthRepository
import com.fitmate.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository
) : ViewModel()
