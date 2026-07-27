package com.gepetto.funhouse.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import club.gepetto.composeutils.textAsBitmap
import club.gepetto.composeutils.toImageBitmap
import club.gepetto.GcLog
import org.jetbrains.compose.resources.imageResource
import com.funhouse.shared.common.generated.resources.*
import com.funhouse.shared.common.generated.resources.funhouseicon

@Composable
fun getBitmap (
    imageFile: String? = null,
    imageResource: org.jetbrains.compose.resources.DrawableResource? = null,
    imageBitmap: ImageBitmap? = null,
    onUpdate: () -> Unit = {},
) : ImageBitmap? {

    if (imageFile != null && imageFile.startsWith("word")) {
        val words = imageFile.split(" ")
        if (words.size > 1) {
            GcLog.v("rendering ${words[1]} as bitmap")
            return textAsBitmap(
                text = words[1],
                textColor = if (isSystemInDarkTheme()) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
            ).toImageBitmap()
        }
    }

    if (imageBitmap != null)
        return imageBitmap

    if (imageResource != null) {
        return imageResource(imageResource)
    }

    return null
}
