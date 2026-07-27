package com.funhouse.shared.common.utils

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler

@Composable
actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}
