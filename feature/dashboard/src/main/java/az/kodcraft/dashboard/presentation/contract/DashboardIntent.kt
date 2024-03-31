package az.kodcraft.dashboard.presentation.contract

import java.time.LocalDate

sealed class DashboardIntent {
    data object Init : DashboardIntent()
    data object GetWeekData : DashboardIntent()
    data class GetWeekWorkouts(val date:LocalDate) : DashboardIntent()
    data class SetDay(val date:LocalDate): DashboardIntent()
}
