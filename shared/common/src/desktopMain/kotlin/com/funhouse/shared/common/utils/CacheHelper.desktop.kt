package com.funhouse.shared.common.utils

import org.jetbrains.compose.resources.StringResource
import java.util.concurrent.ConcurrentHashMap

actual fun getCacheMap(): MutableMap<StringResource, String> = ConcurrentHashMap()
