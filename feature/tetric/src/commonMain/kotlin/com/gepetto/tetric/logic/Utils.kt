package com.gepetto.tetric.logic

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight


fun Offset(x: Int, y: Int) = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())

enum class Direction {
    Left, Up, Right, Down
}

fun Direction.toOffset() = when (this) {
    Direction.Left -> -1 to 0
    Direction.Up -> 0 to -1
    Direction.Right -> 1 to 0
    Direction.Down -> 0 to 1
}

val LedFontFamily = FontFamily.Default

val NextMatrix = 4 to 2
const val ScoreEverySpirit = 12

fun calculateScore(lines: Int) = when (lines) {
    1 -> 100
    2 -> 300
    3 -> 700
    4 -> 1500
    else -> 0
}

@SuppressLint("StaticFieldLeak")
object SoundUtil {
    private var _context: Context? = null
    private val sp: SoundPool by lazy {
        SoundPool.Builder().setMaxStreams(4).setMaxStreams(AudioManager.STREAM_MUSIC).build()
    }
    private val _map = mutableMapOf<SoundType, Int>()

    fun init(context: Context) {
        _context = context
        Sounds.forEach {
            var resId = it.res
            if (resId == 0) {
                // Try to find a fallback sound in shared:common
                val fallbackName = when (it) {
                    SoundType.Move -> "coin"
                    SoundType.Rotate -> "flipcard"
                    SoundType.Start -> "dice"
                    SoundType.Drop -> "boing"
                    SoundType.Clean -> "jackpot"
                }
                resId = context.resources.getIdentifier(fallbackName, "raw", "com.funhouse.shared.common.android")
            }

            if (resId != 0) {
                _map[it] = sp.load(_context, resId, 1)
            }
        }
    }

    fun release() {
        _context = null
        sp.release()
    }

    fun play(isMute: Boolean, sound: SoundType) {
        if (!isMute) {
            val id = _map[sound]
            if (id != null) {
                sp.play(id, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}

sealed class SoundType(val res: Int) {
    object Move : SoundType(0)
    object Rotate : SoundType(0)
    object Start : SoundType(0)
    object Drop : SoundType(0)
    object Clean : SoundType(0)
}

val Sounds = listOf(SoundType.Move, SoundType.Rotate, SoundType.Start, SoundType.Drop, SoundType.Clean)