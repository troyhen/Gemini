package gemini.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import gemini.foundation.Draw
import gemini.foundation.Thing
import gemini.geometry.Rectangle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.time.TimeSource

class Stage(
    private val scope: CoroutineScope,
    val textMeasurer: TextMeasurer,
    startImmediately: Boolean = true,
    private val coroutineContext: CoroutineContext = Dispatchers.Default,
) : SceneScope(), Draw {

    private val thingsLock = Any()

    @Volatile
    private var toAct: List<Thing>? = null

    @Volatile
    private var toDraw: List<Thing>? = null

    private val collisionArea1 = Rectangle()
    private val collisionArea2 = Rectangle()

    private var frame by mutableIntStateOf(0)
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            timeMark = now
        }
    }

    private fun actors(): List<Thing> {
        return toAct ?: synchronized(thingsLock) {
            toAct ?: things.toList().also {
                toAct = it
            }
        }
    }

    override fun add(thing: Thing) {
        synchronized(thingsLock) {
            super.add(thing)
            toAct = null
            toDraw = null
        }
        step()
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

    override fun DrawScope.draw() {
        val all = drawables()
        all.fastForEach { thing ->
            if (thing is BeforeCamera) {
                thing.run { draw() }
            }
        }
        withTransform({
            camera.run { transform() }
        }) {
            all.fastForEach { thing ->
                if (thing is BeforeCamera || thing is AfterCamera) return@fastForEach
                thing.run { draw() }
            }
        }
        all.fastForEach { thing ->
            if (thing is AfterCamera) {
                thing.run { draw() }
            }
        }
        if (isRunning) frame++ // triggers recompose
        frameFlow.value = frame // trigger actions if the frame changed
    }

    private fun drawables(): List<Thing> {
        return toDraw ?: synchronized(thingsLock) {
            toDraw ?: things.toList().also {
                toDraw = it
            }
        }
    }

    fun dispose() {
        stop()
        job?.cancel()
        job = null
        release()
        if (instance === this) {
            instance = null
        }
    }

    suspend fun load(scene: Scene) {
        replaceAll(scene)
        assets.forEach { (source, asset) ->
            asset.load(source)
        }
        step()
    }

    private fun loop() {
        timeMark = time.markNow()
        job?.cancel() // ensure only one job is running at a time
        job = frameFlow.onEach {
            act()
        }.launchIn(scope)
    }

    override fun release() {
        synchronized(thingsLock) {
            super.release()
            toAct = null
            toDraw = null
        }
    }

    override fun remove(thing: Thing) {
        synchronized(thingsLock) {
            super.remove(thing)
            toAct = null
            toDraw = null
        }
        step()
    }

    override fun replaceAll(scene: Scene) {
        synchronized(thingsLock) {
            super.replaceAll(scene)
            toAct = null
            toDraw = null
        }
        step()
    }

    fun start() {
        if (!isRunning) {
            timeMark = time.markNow()
            isRunning = true
        }
    }

    fun step() {
        if (isRunning) return
        timeMark = time.markNow()
        ++frame // Trigger recompose. Actions will also be triggered once after draw.
    }

    fun stop() {
        isRunning = false
    }

    companion object {
        private val defaultCamera = Camera()
        var instance: Stage? = null
            private set
        val time = TimeSource.Monotonic
        val camera get() = instance?.camera ?: defaultCamera
        val visible get() = camera.visible
        val world get() = camera.world
    }
}
