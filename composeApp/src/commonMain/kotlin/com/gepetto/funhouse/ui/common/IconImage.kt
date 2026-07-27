package com.gepetto.funhouse.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.Constants
import club.gepetto.GcLog

@Composable
fun IconImage (
    modifier : Modifier = Modifier,
    imageFile: String? = null,
    imageResource: org.jetbrains.compose.resources.DrawableResource?  = null,
    imageBitmap: ImageBitmap? = null,
    imageVector: ImageVector? = null,
    contentDescription : String? = null,
    onUpdate: (Long) -> Unit = {},
    onClick: () -> Unit = {},
) {
    val thisTimeStamp = remember { mutableStateOf(0L) }
    GcLog.d("IconImage '${imageFile}' on NavGraph called time stamp = ${thisTimeStamp.value}")

    val imageVariableBitmap = getBitmap(
        imageFile = imageFile,
        imageResource = imageResource,
        imageBitmap = imageBitmap,
        onUpdate = {
            thisTimeStamp.value = System.currentTimeMillis()
            GcLog.d("IconImage '${imageFile}' on NavGraph updated time stamp to ${thisTimeStamp.value}")
            onUpdate(thisTimeStamp.value)
        }
    )

    var newModifier = modifier
        .padding(4.dp)
        .clip(RoundedCornerShape(corner = CornerSize(16.dp)))

    if (onClick != {} )
        newModifier = newModifier.clickable { onClick() }

    val loadedBitmap = remember(imageFile) {
        if (imageFile != null && !imageFile.startsWith("word")) {
            com.funhouse.shared.common.utils.loadImageBitmapFromFile(imageFile)
        } else {
            null
        }
    }

    if (loadedBitmap != null) {
        GcImage(
            imageBitmap = loadedBitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = newModifier
        )
    }
    else if (imageFile != null && !imageFile.startsWith("word")) {
        GcImage(
            imageBitmap = getBitmap(imageResource = Constants.DEFAULT_IMAGE),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = newModifier
        )
    }
    else if (imageVariableBitmap != null) {
        GcImage(
            imageBitmap = imageVariableBitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = newModifier
        )
    }
    else if (imageVector != null) {
        GcImage(
            imageVector = imageVector,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = newModifier
        )
    }
}

@Composable
fun GcImage(
    imageBitmap: ImageBitmap? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
) {
    if (imageBitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (imageVector != null) {
        androidx.compose.material3.Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}
