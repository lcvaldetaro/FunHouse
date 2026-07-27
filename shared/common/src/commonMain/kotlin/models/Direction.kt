package com.funhouse.shared.common.models

import kotlinx.serialization.Serializable

@Serializable
data class Direction (
    val label: String,
    val command: String
)
