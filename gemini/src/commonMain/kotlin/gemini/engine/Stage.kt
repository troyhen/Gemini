package gemini.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import gemini.fastForEach
import gemini.fastForEachIndexed
import gemini.foundation.Thing
import gemini.geometry.Rectangle
import gemini.geometry.World
import gemini.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.TimeSource

class Stage(
    private val scope: CoroutineScope,
    val textMeasurer: TextMeasurer,
    startImmediately: Boolean = true,
    private val coroutineContext: CoroutineContext = Dispatchers.Default,
) : SceneScope() {
    var screenSize: Size = Size.Zero
        private set
    private var toAct: List<Thing>? = null
    private var toDraw: List<Thing>? = null

    private val collisionArea1 = Rectangle()
    private val collisionArea2 = Rectangle()

    private var frame by mutableStateOf(0)
    private val frameFlow = MutableStateFlow(0)
    private var timeMark = time.markNow()

    var isRunning by mutableStateOf(startImmediately)
        private set
    private var job: Job? = null

    init {
        instance = this
        loop()
    }

    private suspend fun act() = withContext(coroutineContext) {
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
        val center = center
        val scale = center.max
        if (size.width == size.height) {
            visible.set(world)
        } else if (size.width > size.height) {
            val y = .5f * (world.bottom + world.top)
            val s2 = .5f * size.height / size.width
            val h2 = s2 * (world.bottom - world.top)
            visible.set(world.left, world.right, y - h2, y + h2, world.near, world.far)
        } else {
            val x = .5f * (world.right + world.left)
            val s2 = .5f * size.width / size.height
            val w2 = s2 * (world.right - world.left)
            visible.set(x - w2, x + w2, world.top, world.bottom, world.near, world.far)
        }

        drawContext.transform.translate(center.x, center.y)
        drawContext.transform.scale(scale, scale, Offset.Zero)
        drawContext.transform.transform(camera.matrix)
        drawables().fastForEach { thing ->
            thing.run {
                draw()
            }
        }
        if (isRunning) frame++ // triggers recompose
        frameFlow.value = frame // trigger actions if the frame changed
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
            set(scene.world)
        }
    }

    private fun loop() {
        timeMark = time.markNow()
        job?.cancel() // ensure only one job is running at a time
        job = frameFlow.onEach {
            act()
        }.launchIn(scope)
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

    fun set(world: World) {
        this.world = world
    }

    fun start() {
        isRunning = true
    }

    fun step() {
        if (isRunning) return
        ++frame // Trigger recompose. Actions will be triggered after draw.
    }

    fun stop() {
        isRunning = false
    }

    companion object {
        var instance: Stage? = null
            private set
        val screenSize: Size get() = instance?.screenSize ?: Size(1f, 1f)
        val time = TimeSource.Monotonic
        val visible get() = instance?.visible ?: World()
        val world get() = instance?.world ?: World()
    }
}
