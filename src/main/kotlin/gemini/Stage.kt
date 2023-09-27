package gemini

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.TimeSource

class Stage {
    private var camera: Camera = Camera()
    private var frame by mutableStateOf(0)
    private var things = mutableListOf<Thing>()
    private var thread: Thread? = null

    val isRunning get() = thread != null

    private suspend fun act(elapsed: Duration) {
        things.forEach { thing ->
            (thing as? Actor)?.act(elapsed)
        }
    }

    fun add(thing: Thing) {
        things.add(thing)
    }

    fun addAll(things: Iterable<Thing>) {
        this.things.addAll(things)
    }

    fun DrawScope.draw() {
        drawContext.transform.transform(camera.matrix)
        things.fastForEach { thing ->
            thing.run {
                draw()
            }
        }
        frame++
    }

    private fun loop() {
        var last = time.markNow()
        runBlocking {
            while (isRunning) {
                delay(10)
                val now = time.markNow()
                val elapsed = now - last
                act(elapsed)
                last = now
            }
        }
    }

    fun set(camera: Camera) {
        this.camera = camera
    }

    @Synchronized
    fun start() {
        if (thread == null) {
            thread = thread(isDaemon = true, name = "Gemini update") {
                loop()
            }
        }
    }

    @Synchronized
    fun stop() {
        thread = null
    }

    companion object {
        private val time = TimeSource.Monotonic
    }
}
