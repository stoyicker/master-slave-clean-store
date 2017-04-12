package domain.entity

internal const val HOUR = "hour"
internal const val DAY = "day"
internal const val WEEK = "week"
internal const val MONTH = "month"
internal const val YEAR = "year"
internal const val ALL_TIME = "all"

enum class TimeRange(val value: String) {
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year"),
    ALL_TIME("all")
}
