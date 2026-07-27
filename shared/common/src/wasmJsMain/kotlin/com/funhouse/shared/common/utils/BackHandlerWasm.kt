package com.funhouse.shared.common.utils

import androidx.compose.runtime.Composable

@Composable
actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No physical back button in Web browser
}
