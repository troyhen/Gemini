package gemini.asset

import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

actual class Sound : Asset() {

    private var clip: Clip? = null
    actual override val isLoaded: Boolean get() = clip?.isOpen == true

    actual override suspend fun load(source: Source): Unit {
        withContext(Dispatchers.IO) {
            release()
            val audioInputStream = when (source) {
                is SourceFile -> AudioSystem.getAudioInputStream(source.file)
                is SourceUrl -> {
                    val resource = javaClass.classLoader.getResource(source.url.path)
                        ?: error("Resource not found: ${source.url.path}")
                    AudioSystem.getAudioInputStream(resource)
                }

                is SourceURL -> AudioSystem.getAudioInputStream(source.url)
                else -> error("Not supported: $source")
            }
            audioInputStream.use { stream ->
                open(stream)
            }
        }
    }

    private fun open(audioInputStream: AudioInputStream) {
        clip = AudioSystem.getClip().apply {
            open(audioInputStream)
        }
    }

    actual fun play(times: Int) {
        if (times <= 0) return
        clip?.apply {
            stop()
            framePosition = 0
            loop(times - 1)
        }
    }

    actual override fun release() {
        clip?.close()
        clip = null
    }

    actual fun stop() {
        clip?.stop()
    }
}

val Url.path: String get() = encodedPath.removePrefix("/")
