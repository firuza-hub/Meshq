package az.kodcraft.workout.presentation.createWorkout.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import az.kodcraft.core.R
import az.kodcraft.core.presentation.bases.BasePreviewContainer
import az.kodcraft.core.presentation.composable.textField.TextFieldSingleLineBox
import az.kodcraft.core.presentation.theme.PrimaryTurq
import az.kodcraft.core.presentation.theme.bodySmallLight
import az.kodcraft.core.presentation.theme.largeHeadLine
import az.kodcraft.core.utils.noRippleClickable
import az.kodcraft.workout.domain.model.CreateWorkoutDm
import kotlin.random.Random
import kotlin.random.nextULong

@Composable
fun AddExercise(
    modifier: Modifier = Modifier,
    exercise: CreateWorkoutDm.Exercise,
    onSaveExerciseSets: (List<CreateWorkoutDm.Exercise.Set>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {

    var setsInEdit by remember {
        mutableStateOf(exercise.sets)
    }

    var lastSet by remember {
        mutableStateOf(CreateWorkoutDm.Exercise.Set.EMPTY)
    }
    var lastSetApplied by remember { mutableStateOf(false) }

    LaunchedEffect(lastSet) {
        lastSetApplied = lastSet.reps != "0"
    }
    Column(modifier = modifier) {
        Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "",
                modifier = Modifier.noRippleClickable { onDismiss() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.largeHeadLine.copy(Color.White)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                tint = PrimaryTurq,
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .noRippleClickable { onSaveExerciseSets(if (lastSetApplied) setsInEdit + lastSet else setsInEdit) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        AddExerciseSetsCard(
            setsInEdit,
            lastSet = lastSet,
            lastSetApplied = lastSetApplied,
            updateLastSet = { lastSet = it },
            updateSets = { setsInEdit = it }
        )
    }
}

@Composable
fun AddExerciseSetsCard(
    setsInEdit: List<CreateWorkoutDm.Exercise.Set>,
    updateSets: (List<CreateWorkoutDm.Exercise.Set>) -> Unit,
    lastSet: CreateWorkoutDm.Exercise.Set,
    lastSetApplied: Boolean,
    updateLastSet: (CreateWorkoutDm.Exercise.Set) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.LightGray.copy(0.1f))
            .padding(vertical = 16.dp)
    ) {

        val isEditable = true
        ExerciseSetsHeader()
        Spacer(modifier = Modifier.height(18.dp))
        setsInEdit.forEach { set ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SetTypeDropdown(
                    selectedType = set.type, onTypeSelected = { v ->
                        updateSets(
                            setsInEdit.map { if (it.id == set.id) it.copy(type = v) else it })
                    }, modifier = Modifier
                        .weight(1.5f)
                )

                TextFieldSingleLineBox(
                    isEditable = isEditable,
                    value = set.reps,
                    onValueChange = { v ->
                        updateSets(
                            setsInEdit.map { if (it.id == set.id) it.copy(reps = v) else it })
                    },
                    textStyle = MaterialTheme.typography.bodySmallLight,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                )


                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .weight(1.5f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextFieldSingleLineBox(
                        isEditable = isEditable,
                        value = set.weight,
                        onValueChange = { v ->
                            updateSets(
                                setsInEdit.map { if (it.id == set.id) it.copy(weight = v) else it })
                        },
                        textStyle = MaterialTheme.typography.bodySmallLight,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = set.unit,
                        style = MaterialTheme.typography.bodySmallLight,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .weight(1.5f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextFieldSingleLineBox(
                        isEditable = isEditable,
                        value = set.restSeconds,
                        onValueChange = { v ->
                            updateSets(
                                setsInEdit.map { if (it.id == set.id) it.copy(restSeconds = v) else it })
                        },
                        textStyle = MaterialTheme.typography.bodySmallLight,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "sec",
                        style = MaterialTheme.typography.bodySmallLight,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(modifier = Modifier
                    .noRippleClickable {
                        updateSets(setsInEdit.filterNot { it.id == set.id })
                    }
                    .width(24.dp)) {
                    Icon(
                        painterResource(id = R.drawable.ic_remove_circle),
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                            .align(Alignment.Center),
                        contentDescription = "set complete"
                    )

                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            SetTypeDropdown(
                selectedType = lastSet.type, onTypeSelected = { v ->
                    updateLastSet(lastSet.copy(type = v))
                }, modifier = Modifier
                    .weight(1.5f),
                color = if (lastSetApplied) Color.White else Color.White.copy(0.5f)
            )


            TextFieldSingleLineBox(
                isEditable = isEditable,
                value = lastSet.reps,
                onValueChange = { v ->  updateLastSet(  lastSet.copy(reps = v)) },
                textStyle = MaterialTheme.typography.bodySmallLight.copy(
                    color = if (lastSetApplied) Color.White else Color.White.copy(
                        0.5f
                    )
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            )


            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .weight(1.5f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFieldSingleLineBox(
                    isEditable = isEditable,
                    value = lastSet.weight,
                    onValueChange = { v ->  updateLastSet(lastSet.copy(weight = v)) },
                    textStyle = MaterialTheme.typography.bodySmallLight.copy(
                        color = if (lastSetApplied) Color.White else Color.White.copy(
                            0.5f
                        )
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = lastSet.unit,
                    style = MaterialTheme.typography.bodySmallLight.copy(
                        color = if (lastSetApplied) Color.White else Color.White.copy(
                            0.5f
                        )
                    ),
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .weight(1.5f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFieldSingleLineBox(
                    isEditable = isEditable,
                    value = lastSet.restSeconds,
                    onValueChange = { v ->  updateLastSet(lastSet.copy(restSeconds = v)) },
                    textStyle = MaterialTheme.typography.bodySmallLight.copy(
                        color = if (lastSetApplied) Color.White else Color.White.copy(
                            0.5f
                        )
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "sec",
                    style = MaterialTheme.typography.bodySmallLight.copy(
                        if (lastSetApplied) Color.White else Color.White.copy(
                            0.5f
                        )
                    ),
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(modifier = Modifier
                .noRippleClickable {
                    val newId = Random
                        .nextULong()
                        .toString()
                    updateSets(setsInEdit + lastSet.copy(id = newId))
                    // lastSet = CreateWorkoutDm.Exercise.Set.EMPTY
                }
                .width(24.dp)) {
                Icon(
                    painterResource(id = R.drawable.ic_add_circle),
                    tint = PrimaryTurq.copy(0.7f),
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                        .align(Alignment.Center),
                    contentDescription = "set complete"
                )

            }
        }
    }
}

@Composable
fun ExerciseSetsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "set type",
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .weight(1.5f),
            style = MaterialTheme.typography.bodySmallLight.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = "reps",
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = "weight",
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .weight(1.5f),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = "rest",
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = "",
            maxLines = 1,
            modifier = Modifier
                .width(24.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}


@Composable
fun SetTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    var expanded by remember { mutableStateOf(false) }
    // Generate month names using LocalDate and the default locale
    val types = listOf("warmup", "working", "failure")

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedType,
                style = MaterialTheme.typography.bodySmallLight.copy(color = color),
                modifier = Modifier
                    .noRippleClickable { expanded = true }
                    .padding(end = 4.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_dropdown),
                contentDescription = "type dropdown",
                modifier = Modifier.size(16.dp),
                tint = color
            )
        }

        // Dropdown menu that shows when expanded is true
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .height(160.dp)  // Adjust height to fit the UI design
                .padding(top = 4.dp)
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type, style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun PreviewAddExercise() = BasePreviewContainer {
    AddExercise(exercise = CreateWorkoutDm.Exercise.MOCK)
}