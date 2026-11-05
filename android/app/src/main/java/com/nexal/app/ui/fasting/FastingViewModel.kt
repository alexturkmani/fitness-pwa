package com.nexal.app.ui.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.ActiveFast
import com.nexal.app.data.repository.FastingMath
import com.nexal.app.data.repository.FastingPlan
import com.nexal.app.data.repository.FastingRepository
import com.nexal.app.data.repository.RewardRepository
import com.nexal.app.data.repository.RewardType
import com.nexal.app.data.repository.fastingPlans
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FastingUiState(
    val selectedPlan: FastingPlan = fastingPlans.first { it.id == "16_8" },
    val activeFast: ActiveFast? = null,
    val activePlan: FastingPlan? = null,
    val elapsedMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val progress: Float = 0f,
    val goalReached: Boolean = false,
)

@HiltViewModel
class FastingViewModel @Inject constructor(
    private val fasting: FastingRepository,
    private val rewards: RewardRepository,
) : ViewModel() {

    private val selectedPlan = MutableStateFlow(fastingPlans.first { it.id == "16_8" })

    private val clock = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    val uiState: StateFlow<FastingUiState> =
        combine(selectedPlan, fasting.active, clock) { selected, active, now ->
            val plan = active?.let { fast -> fastingPlans.find { it.id == fast.planId } }
            if (active == null || plan == null) {
                FastingUiState(selectedPlan = selected)
            } else {
                val elapsed = FastingMath.elapsedMillis(active, now)
                FastingUiState(
                    selectedPlan = selected,
                    activeFast = active,
                    activePlan = plan,
                    elapsedMillis = elapsed,
                    remainingMillis = (FastingMath.goalMillis(plan) - elapsed).coerceAtLeast(0L),
                    progress = FastingMath.progress(active, plan, now),
                    goalReached = FastingMath.isGoalReached(active, plan, now),
                )
            }
        }.onEach { state ->
            // Keyed to this fast, so it pays once however often the clock ticks.
            val fast = state.activeFast
            if (state.goalReached && fast != null) {
                rewards.award(RewardType.FAST, dedupeKey = fast.startedAtMillis.toString())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FastingUiState())

    fun selectPlan(plan: FastingPlan) {
        selectedPlan.value = plan
    }

    fun startFast() = fasting.start(selectedPlan.value.id)

    fun endFast() = fasting.end()
}
