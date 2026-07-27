package com.funhouse.shared.common.utils

fun String.lettersOnly() : Boolean {
    return this.all { it.isLetter() }
}

fun String.formatWithArgs(args: Array<out Any?>): String {
    var result = this
    for (arg in args) {
        val str = arg?.toString() ?: "null"
        val regex = Regex("%[a-zA-Z]")
        val match = regex.find(result)
        if (match != null) {
            result = result.replaceFirst(match.value, str)
        } else {
            break
        }
    }
    return result
}
