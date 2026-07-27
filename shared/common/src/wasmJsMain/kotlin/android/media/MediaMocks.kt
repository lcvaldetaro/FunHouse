package android.media

object AudioManager {
    const val STREAM_MUSIC = 3
}

class SoundPool {
    fun load(context: Any?, resId: Int, priority: Int): Int = 0
    fun play(soundID: Int, leftVolume: Float, rightVolume: Float, priority: Int, loop: Int, rate: Float): Int = 0
    fun release() {}
    
    class Builder {
        fun setMaxStreams(maxStreams: Int): Builder = this
        fun build(): SoundPool = SoundPool()
    }
}
