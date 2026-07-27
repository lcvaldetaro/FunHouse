package com.funhouse.shared.common.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.funhouse.shared.common.android.R
import com.funhouse.shared.common.AppData
import java.io.File

private fun Game.gameBitMap(ctx: Context, useIcon: Boolean) : Bitmap {
    var bitmap =
        if (gameImage != null && gameImage.fileName.isNotEmpty())
            BitmapFactory.decodeFile("${AppData.packageFolder}/${AppData.gameFolder}/${gameImage.fileName}")
        else
        if (localGame != null)
            BitmapFactory.decodeResource(ctx.resources, if (useIcon) localGame.icon else localGame.image)
        else
            BitmapFactory.decodeResource(ctx.resources, R.drawable.funhouse)

    if (bitmap == null) {
        bitmap = when (nickName) {
            "island" -> BitmapFactory.decodeResource(ctx.resources, R.drawable.island)
            "funhouse" -> BitmapFactory.decodeResource(ctx.resources, R.drawable.funhouse)
            else -> BitmapFactory.decodeResource(ctx.resources, R.drawable.funhouse)
        }
    }

    return bitmap!!
}

fun Game.gameBitMap(ctx: Context) : Bitmap {
    return gameBitMap(ctx, false)
}

fun Game.gameIconBitMap(ctx: Context) : Bitmap {
    return gameBitMap(ctx, true)
}
