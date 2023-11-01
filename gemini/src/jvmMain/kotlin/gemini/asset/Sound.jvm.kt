package gemini.asset

import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

actual class Sound : Asset() {

    private var clip: Clip = AudioSystem.getClip()
    actual override val isLoaded: Boolean get() = clip.isOpen

    actual override suspend fun load(source: Source): Unit {
        withContext(Dispatchers.IO) {
            Sound().apply {
                val audioInputStream = when (source) {
                    is SourceFile -> AudioSystem.getAudioInputStream(source.file)
                    is SourceUrl -> AudioSystem.getAudioInputStream(javaClass.classLoader.getResource(source.url.path))
                    is SourceURL -> AudioSystem.getAudioInputStream(source.url)
                    else -> error("Not supported: $source")
                }
                open(audioInputStream)
            }
        }
    }

    private fun open(audioInputStream: AudioInputStream) {
        clip.open(audioInputStream)
    }

    actual fun play(times: Int) {
//        clip.setLoopPoints(0, -1)
        clip.loop(times)
    }

    actual override fun release() {
        clip.stop()
    }

    actual fun stop() {
        clip.stop()
    }
}

val Url.path: String get() = encodedPath.removePrefix("/")
