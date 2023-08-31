package me.fluxcapacitor2.todoapp.utils

import me.fluxcapacitor2.todoapp.api.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private fun formatter(pattern: String) = DateTimeFormatter
    .ofPattern(pattern)
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())!!

private fun ZonedDateTime.format(pattern: String) = this.format(formatter(pattern))

fun Task.formatDueDate() = shortDateFormat(dueDate!!)
fun Task.formatStartDate() = shortDateFormat(startDate!!)

fun shortDateFormat(isoDateString: String): String {

    val date = ZonedDateTime
        .parse(
            isoDateString, DateTimeFormatter.ISO_DATE_TIME
        )
    val now = ZonedDateTime.now(ZoneId.systemDefault())

    val midnight = LocalTime.MIDNIGHT
    val today = ZonedDateTime.now(ZoneId.systemDefault())
    val todayMidnight = ZonedDateTime.of(today.toLocalDate(), midnight, ZoneId.systemDefault())
    val startOfTomorrow = todayMidnight.plusDays(1)
    val endOfTomorrow = startOfTomorrow.plusDays(1)
    val startOfYesterday = todayMidnight.minusDays(1)

    val isThisYear = now.year == date.year

    val dayDifference =
        ChronoUnit.DAYS.between(date, now)

    val isWithinOneWeek = dayDifference in 1..6
    val isTomorrow = isBetween(date, startOfTomorrow, endOfTomorrow)
    val isYesterday = isBetween(date, startOfYesterday, todayMidnight)
    val isLastWeek = dayDifference > -7 && dayDifference < 0

    val isMidnight =
        date.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime() == LocalTime.MIDNIGHT

    val day = if (isYesterday) {
        "Yesterday"
    } else if (date.toLocalDate() == LocalDate.now()) {
        if (isMidnight) {
            "Today"
        } else {
            ""
        }
    } else if (isTomorrow) {
        "Tomorrow"
    } else if (isLastWeek) {
        "Last ${date.format("EEE")}"
    } else if (isWithinOneWeek) {
        date.format("MMM d")
    } else {
        date.format("EEE")
    }

    val time =
        if (isThisYear && !isMidnight) date.format("h:mm a") else ""

    val year = if (!isThisYear) date.format("YYYY") else ""

    return "${day}${if (day.isNotEmpty() && time.isNotEmpty()) ", " else ""}${time}${if (year.isNotEmpty()) ", " else ""}${year}"
}

private fun isBetween(
    first: ZonedDateTime,
    dateToCompare: ZonedDateTime,
    second: ZonedDateTime
): Boolean {
    return dateToCompare.isAfter(first) && dateToCompare.isBefore(second)
}