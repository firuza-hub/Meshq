package az.kodcraft.workout.presentation.workoutProgress.contract

import android.os.Parcelable
import az.kodcraft.workout.domain.model.WorkoutDm
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutProgressUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val workout: WorkoutDm = WorkoutDm.MOCK
) : Parcelable {
    sealed class PartialState {
        data object Loading : PartialState()
        data class WorkoutData(val data: WorkoutDm) : PartialState()
        data class ExerciseStatus(val exerciseId: String) : PartialState()
    }
}