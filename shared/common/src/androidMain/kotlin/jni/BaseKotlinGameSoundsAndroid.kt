package com.funhouse.shared.common.jni

import android.media.MediaPlayer
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.android.R

object AndroidSoundPlayer {
    var bicyclePlayer: MediaPlayer? = null
    var coinPlayer: MediaPlayer? = null
    var bellPlayer: MediaPlayer? = null
    var jackpotPlayer: MediaPlayer? = null
    var jackpotBiggerPlayer: MediaPlayer? = null
    var jackpotMusicPlayer: MediaPlayer? = null
    var tennisBallPLayer: MediaPlayer? = null
    var flipPlayer: MediaPlayer? = null
    var chipPlayer: MediaPlayer? = null
    var bumpPlayer: MediaPlayer? = null
    var boingPlayer: MediaPlayer? = null
    var dicePlayer: MediaPlayer? = null

    fun initialize() {
        val ctx = AppData.applicationContext as? android.content.Context ?: return
        try {
            bicyclePlayer = MediaPlayer.create(ctx, R.raw.bicycle).apply { setOnErrorListener { _, _, _ -> true } }
            coinPlayer = MediaPlayer.create(ctx, R.raw.coin).apply { setOnErrorListener { _, _, _ -> true } }
            bellPlayer = MediaPlayer.create(ctx, R.raw.bell).apply { setOnErrorListener { _, _, _ -> true } }
            jackpotPlayer = MediaPlayer.create(ctx, R.raw.jackpot).apply { setOnErrorListener { _, _, _ -> true } }
            jackpotBiggerPlayer = MediaPlayer.create(ctx, R.raw.jackpotbigger).apply { setOnErrorListener { _, _, _ -> true } }
            jackpotMusicPlayer = MediaPlayer.create(ctx, R.raw.jackpotmusic).apply { setOnErrorListener { _, _, _ -> true } }
            flipPlayer = MediaPlayer.create(ctx, R.raw.flipcard).apply { setOnErrorListener { _, _, _ -> true } }
            tennisBallPLayer = MediaPlayer.create(ctx, R.raw.tennisball).apply { setOnErrorListener { _, _, _ -> true } }
            chipPlayer = MediaPlayer.create(ctx, R.raw.chips).apply { setOnErrorListener { _, _, _ -> true } }
            bumpPlayer = MediaPlayer.create(ctx, R.raw.bumperhit).apply { setOnErrorListener { _, _, _ -> true } }
            boingPlayer = MediaPlayer.create(ctx, R.raw.boing).apply { setOnErrorListener { _, _, _ -> true } }
            dicePlayer = MediaPlayer.create(ctx, R.raw.bumperhit).apply { setOnErrorListener { _, _, _ -> true } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

actual fun playBicycle() {
    if (AndroidSoundPlayer.bicyclePlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.bicyclePlayer?.start() } catch (e: Exception) {}
}
actual fun haltBicycle() {
    try { AndroidSoundPlayer.bicyclePlayer?.pause() } catch (e: Exception) {}
}
actual fun playCoin() {
    if (AndroidSoundPlayer.coinPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.coinPlayer?.start() } catch (e: Exception) {}
}
actual fun playBell() {
    if (AndroidSoundPlayer.bellPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.bellPlayer?.start() } catch (e: Exception) {}
}
actual fun playJackpot() {
    if (AndroidSoundPlayer.jackpotPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.jackpotPlayer?.start() } catch (e: Exception) {}
}
actual fun playJackpotBigger() {
    if (AndroidSoundPlayer.jackpotBiggerPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.jackpotBiggerPlayer?.start() } catch (e: Exception) {}
}
actual fun playJackpotMusic() {
    if (AndroidSoundPlayer.jackpotMusicPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.jackpotMusicPlayer?.start() } catch (e: Exception) {}
}
actual fun playTennisBall() {
    if (AndroidSoundPlayer.tennisBallPLayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.tennisBallPLayer?.start() } catch (e: Exception) {}
}
actual fun playFlip() {
    if (AndroidSoundPlayer.flipPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.flipPlayer?.start() } catch (e: Exception) {}
}
actual fun playChip() {
    if (AndroidSoundPlayer.chipPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.chipPlayer?.start() } catch (e: Exception) {}
}
actual fun playBump() {
    if (AndroidSoundPlayer.bumpPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.bumpPlayer?.start() } catch (e: Exception) {}
}
actual fun playBoing() {
    if (AndroidSoundPlayer.boingPlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.boingPlayer?.start() } catch (e: Exception) {}
}
actual fun playDice() {
    if (AndroidSoundPlayer.dicePlayer == null) AndroidSoundPlayer.initialize()
    try { AndroidSoundPlayer.dicePlayer?.start() } catch (e: Exception) {}
}
