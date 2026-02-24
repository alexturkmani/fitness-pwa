package com.nexal.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository
) : ViewModel()
