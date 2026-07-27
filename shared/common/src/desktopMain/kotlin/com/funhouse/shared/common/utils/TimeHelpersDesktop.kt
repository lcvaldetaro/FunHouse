package com.funhouse.shared.common.utils

import java.util.Calendar

actual fun getCurrentTime(): Pair<Int, Int> {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
}
