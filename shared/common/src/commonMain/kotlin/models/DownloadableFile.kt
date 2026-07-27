package com.funhouse.shared.common.models

import kotlinx.serialization.Serializable

@Serializable
data class DownloadableFile(
    val fileName: String,
    var downloaded: Boolean = false
)
