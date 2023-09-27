package gemini

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.TimeSource

class Stage(val textMeasurer: TextMeasurer) : Scene() {
    private var camera: Camera = Camera()
    private var frame by mutableStateOf(0)
    private var toAct: List<Actor>? = null
    private var toDraw: List<Thing>? = null
    private var thread: Thread? = null

    val isRunning get() = thread != null

    init {
        instance = this
    }

    private suspend fun act(elapsed: Duration) {
        if (toAct == null) synchronized(this) {
            toAct = actors.toList()
        }
        toAct?.fastForEach { actor ->
            actor(elapsed)
        }
    }

    override fun add(actor: Actor) {
        toAct = null
        super.add(actor)
    }

    override fun add(thing: Thing) {
        toDraw = null
        super.add(thing)
    }

    fun DrawScope.draw() {
        if (toDraw == null) synchronized(this) {
            toDraw = things.toList()
        }
        drawContext.transform.transform(camera.matrix)
        toDraw?.fastForEach { thing ->
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

    override fun replaceAll(scene: Scene) {
        toAct = null
        toDraw = null
        super.replaceAll(scene)
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
        var instance: Stage? = null
            private set
        val time = TimeSource.Monotonic
    }
}
