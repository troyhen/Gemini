package gemini.asset

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class Sound : Asset() {
    private var soundId: Int = -1
    private var streamId: Int = 0

    actual override val isLoaded: Boolean get() = soundId != -1

    actual override suspend fun load(source: Source) = withContext(Dispatchers.IO) {
        release()
        soundId = when (source) {
            is SourceAsset -> {
                val context = context ?: error("Must set Sound.context in order to load from assets")
                context.assets.openFd(source.name).use { fd ->
                    soundPool.load(fd, 1)
                }
            }

            is SourcePath -> soundPool.load(source.path, 1)
            is SourceRes -> {
                val context = context ?: error("Must set Sound.context in order to load resources")
                soundPool.load(context, source.id, 1)
            }

            else -> error("Not supported: $source")
        }
    }

    actual fun play(times: Int) {
        if (isLoaded && times > 0) {
            streamId = soundPool.play(soundId, 1f, 1f, 1, times - 1, 1f)
        }
    }

    actual override fun release() {
        stop()
        if (soundId != -1) {
            soundPool.unload(soundId)
            soundId = -1
        }
    }

    actual fun stop() {
        if (streamId != 0) {
            soundPool.stop(streamId)
            streamId = 0
        }
    }

    companion object {

        @Suppress("StaticFieldLeak")
        var context: Context? = null
            set(value) {
                field = value?.applicationContext ?: value
            }

        private val audioAttributes = AudioAttributes.Builder().run {
            setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            build()
        }

        private val soundPool = SoundPool.Builder().run {
            setMaxStreams(3)
            setAudioAttributes(audioAttributes)
            build()
        }
    }
}