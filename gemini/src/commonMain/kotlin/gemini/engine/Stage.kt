package gemini.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import gemini.fastForEach
import gemini.fastForEachIndexed
import gemini.foundation.Thing
import gemini.geometry.Rectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.time.TimeSource

class Stage(val textMeasurer: TextMeasurer) : SceneScope() {
    private var frame by mutableStateOf(0)
    var screenSize: Size = Size.Zero
        private set
    private var toAct: List<Thing>? = null
    private var toDraw: List<Thing>? = null
    private var thread: Thread? by mutableStateOf(null)

    private val collisionArea1 = Rectangle()
    private val collisionArea2 = Rectangle()
    private var timeMark = time.markNow()

    val isRunning get() = thread != null

    init {
        instance = this
    }

    private suspend fun act() {
        val now = time.markNow()
        val elapsed = now - timeMark
        val actors = actors()
        try {
            actors.fastForEach { actor ->
                actor.act(elapsed)
            }
            collider(actors)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            timeMark = now
        }
    }

    private fun actors(): List<Thing> {
        return toAct ?: let {
            things.toList().also {
                toAct = it
            }
        }
    }

    override fun add(thing: Thing) {
        toAct = null
        toDraw = null
        super.add(thing)
    }

    private fun collider(actors: List<Thing>) {
        actors.fastForEachIndexed { index, actor ->
            if (actor is Collider) {
                actor.position.rectangle(collisionArea1)
                actors.fastForEach(index + 1) { other ->
                    if (other is Collider) {
                        other.position.rectangle(collisionArea2)
                        if (collisionArea1.overlaps(collisionArea2)) {
                            if (!actor.collideWith(other)) {
                                other.collideWith(actor)
                            }
                        }
                    }
                }
            }
        }
    }

    fun DrawScope.draw() {
        screenSize = size
        drawContext.transform.transform(camera.matrix)
        drawables().fastForEach { thing ->
            thing.run {
                orientAndDraw()
            }
        }
        if (isRunning) {
            frame++ // triggers recompose
        }
    }

    private fun drawables(): List<Thing> {
        return toDraw ?: let {
            things.toList().also {
                toDraw = it
            }
        }
    }

    fun load(scene: Scene) {
        replaceAll(scene)
        if (scene is SceneScope) {
            set(scene.camera)
        }
    }

    private fun loop() {
        timeMark = time.markNow()
        runBlocking {
            while (isRunning) {
                delay(15)
                act()
            }
        }
    }

    override fun remove(thing: Thing) {
        toAct = null
        toDraw = null
        super.remove(thing)
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
        if (isRunning) return
        thread = thread(isDaemon = true, name = "Gemini update") {
            loop()
        }
    }

    @Synchronized
    fun step() {
        if (isRunning) return
        runBlocking {
            act()
        }
        frame++ // recompose
    }

    @Synchronized
    fun stop() {
        thread = null
    }

    companion object {
        var instance: Stage? = null
            private set
        val screenSize: Size get() = instance?.screenSize ?: Size.Zero
        val time = TimeSource.Monotonic
    }
}
