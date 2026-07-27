package com.funhouse.shared.common.utils

import org.jetbrains.compose.resources.StringResource

expect fun getCacheMap(): MutableMap<StringResource, String>
