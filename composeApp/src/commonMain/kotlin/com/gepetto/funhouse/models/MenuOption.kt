package com.gepetto.funhouse.models

import androidx.compose.runtime.Composable
import com.gepetto.funhouse.intentprocessors.FunHouseState

data class MenuOption (
    val label: String,
    val state: FunHouseState,
    val function: @Composable () -> Unit = {},
)