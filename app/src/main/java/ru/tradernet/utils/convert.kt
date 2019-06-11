package ru.tradernet.utils

import com.orhanobut.logger.Logger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun fromISO8601UTC(dateStr: String): Date {
    val tz = TimeZone.getTimeZone("UTC")
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    df.setTimeZone(tz)

    return try {
        df.parse(dateStr)
    } catch (e: ParseException) {
        Logger.w(e.message.toString())
        Date(0)
    }
}