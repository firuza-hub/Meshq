package az.kodcraft.workout.presentation.workoutProgress

import androidx.lifecycle.SavedStateHandle
import az.kodcraft.core.domain.bases.model.doOnFailure
import az.kodcraft.core.domain.bases.model.doOnLoading
import az.kodcraft.core.domain.bases.model.doOnNetworkError
import az.kodcraft.core.domain.bases.model.doOnSuccess
import az.kodcraft.core.presentation.bases.BaseViewModel
import az.kodcraft.workout.domain.model.WorkoutDm
import az.kodcraft.workout.domain.usecase.GetWorkoutUseCase
import az.kodcraft.workout.domain.usecase.SaveFinishedWorkoutUseCase
import az.kodcraft.workout.presentation.workoutProgress.contract.WorkoutProgressEvent
import az.kodcraft.workout.presentation.workoutProgress.contract.WorkoutProgressIntent
import az.kodcraft.workout.presentation.workoutProgress.contract.WorkoutProgressUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class WorkoutProgressViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    initialState: WorkoutProgressUiState,
    private val getWorkoutUseCase: GetWorkoutUseCase,
    private val saveFinishedWorkoutUseCase: SaveFinishedWorkoutUseCase
) : BaseViewModel<WorkoutProgressUiState, WorkoutProgressUiState.PartialState, WorkoutProgressEvent, WorkoutProgressIntent>(
    savedStateHandle,
    initialState
) {

    override fun mapIntents(intent: WorkoutProgressIntent): Flow<WorkoutProgressUiState.PartialState> =
        when (intent) {
            is WorkoutProgressIntent.GetWorkoutData -> fetchWorkoutData(intent.workoutId)
            is WorkoutProgressIntent.ChangeExerciseStatus -> flowOf(
                WorkoutProgressUiState.PartialState.ExerciseStatus(
                    intent.exerciseId
                )
            )

            is WorkoutProgressIntent.ToggleExercisePreview -> flowOf(
                WorkoutProgressUiState.PartialState.ToggleExercisePreview(
                    intent.exerciseId
                )
            )

            WorkoutProgressIntent.CompleteWorkout -> flowOf(
                WorkoutProgressUiState.PartialState.CompleteWorkout
            )

            WorkoutProgressIntent.FinishWorkout -> saveFinishedWorkout()
            is WorkoutProgressIntent.ChangeExerciseSetStatus -> flowOf(
                WorkoutProgressUiState.PartialState.ExerciseSetStatus(
                    intent.exerciseId, intent.setId
                )
            )
        }


    override fun reduceUiState(
        previousState: WorkoutProgressUiState,
        partialState: WorkoutProgressUiState.PartialState

    ): WorkoutProgressUiState = when (partialState) {
        WorkoutProgressUiState.PartialState.Loading -> previousState.copy(
            isLoading = true, isError = false
        )

        is WorkoutProgressUiState.PartialState.WorkoutData -> previousState.copy(
            isLoading = false,
            isError = false,
            workout = partialState.data.copy(exercises = partialState.data.exercises.mapIndexed { index, exercise ->
                if (index == 0) exercise.copy(isCurrent = true) else exercise
            })
        )

        is WorkoutProgressUiState.PartialState.ExerciseStatus -> previousState.copy(
            isLoading = false, isError = false,
            workout = previousState.workout.copy(
                exercises = updateExerciseStatus(
                    previousState.workout.exercises,
                    partialState.exerciseId
                )
            )
        )

        WorkoutProgressUiState.PartialState.CompleteWorkout -> previousState.copy(
            isLoading = false, isError = false,
            workout = previousState.workout.copy(
                exercises = previousState.workout.exercises.map { exercise ->
                    exercise.copy(
                        isCurrent = false,
                        sets = exercise.sets.map { set -> set.copy(isComplete = true) })
                }
            )
        )

        is WorkoutProgressUiState.PartialState.ToggleExercisePreview -> previousState.copy(
            workout = previousState.workout.copy(
                exercises = previousState.workout.exercises.map { exercise ->
                    if (exercise.id == partialState.exerciseId)
                        exercise.copy(isInPreviewMode = !exercise.isInPreviewMode)
                    else exercise
                }
            )
        )

        is WorkoutProgressUiState.PartialState.ExerciseSetStatus -> previousState.copy(
            isLoading = false, isError = false,
            workout = previousState.workout.copy(
                exercises = updateExerciseSetStatus(
                    previousState.workout.exercises, partialState.exerciseId, partialState.setId
                )
            )
        )
    }

    private fun updateExerciseStatus(
        exercises: List<WorkoutDm.Exercise>,
        exerciseId: String
    ): List<WorkoutDm.Exercise> {
        // Toggle completion status of the specified exercise and determine new current
        val updatedExercises = exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                val newSets = exercise.sets.map { it.copy(isComplete = !exercise.isComplete()) }
                exercise.copy(sets = newSets)
            } else {
                exercise
            }
        }
        val newCurrentIndex = updatedExercises.indexOfFirst { !it.isComplete() }

        // Update isCurrent status based on new current index
        return updatedExercises.mapIndexed { index, exercise ->
            if (index == newCurrentIndex) {
                exercise.copy(isCurrent = true)
            } else {
                exercise.copy(isCurrent = false)
            }
        }
    }

    private fun updateExerciseSetStatus(
        exercises: List<WorkoutDm.Exercise>,
        exerciseId: String,
        setId: String,
    ): List<WorkoutDm.Exercise> {
        // Toggle completion status of the specified exercise and determine new current
        val updatedExercises = exercises.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(sets = exercise.sets.map { set ->
                    if (set.id == setId) set.copy(
                        isComplete = !set.isComplete
                    ) else set
                })
            } else exercise
        }
        val newCurrentIndex = updatedExercises.indexOfFirst { !it.isComplete() }

        // Update isCurrent status based on new current index
        return updatedExercises.mapIndexed { index, exercise ->
            if (index == newCurrentIndex) {
                exercise.copy(isCurrent = true)
            } else {
                exercise.copy(isCurrent = false)
            }
        }
    }


    private fun fetchWorkoutData(workoutId: String)
            : Flow<WorkoutProgressUiState.PartialState> =
        flow {
            getWorkoutUseCase.execute(
                workoutId
            ).doOnSuccess { data ->
                if (data != null)
                    emit(
                        WorkoutProgressUiState.PartialState.WorkoutData(data)
                    )
            }.doOnFailure {
                // emit(WorkoutProgressUiState.PartialState.Error(it.message.orEmpty()))
            }.doOnLoading {
                emit(WorkoutProgressUiState.PartialState.Loading)
            }.doOnNetworkError {
                //  emit(WorkoutProgressUiState.PartialState.NetworkError)
            }.collect()
        }
    private fun saveFinishedWorkout(): Flow<WorkoutProgressUiState.PartialState> =
        flow {
            saveFinishedWorkoutUseCase.execute(
                uiState.value.workout
            ).doOnSuccess { data ->
                if (data)
                    publishEvent(WorkoutProgressEvent.NavigateHome)
            }.doOnFailure {
                // emit(WorkoutProgressUiState.PartialState.Error(it.message.orEmpty()))
            }.doOnLoading {
                emit(WorkoutProgressUiState.PartialState.Loading)
            }.doOnNetworkError {
                //  emit(WorkoutProgressUiState.PartialState.NetworkError)
            }.collect()
        }


}