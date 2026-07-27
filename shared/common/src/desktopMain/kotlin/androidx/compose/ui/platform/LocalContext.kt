package androidx.compose.ui.platform

import androidx.compose.runtime.Composable

object LocalContext {
    val current: Any?
        @Composable
        get() = null
}
