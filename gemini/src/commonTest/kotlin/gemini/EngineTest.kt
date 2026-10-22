package gemini

import gemini.asset.*
import gemini.engine.SceneScope
import gemini.engine.fastForEach
import gemini.engine.fastForEachIndexed
import gemini.foundation.MovingThing
import gemini.geometry.*
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class EngineTest {

    private fun assertFloatEquals(expected: Float, actual: Float, tolerance: Float = 1e-4f) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "Expected $expected (+/- $tolerance), but was $actual",
        )
    }

    private class TestMovingThing(
        position: Position = Position(),
        velocity: Velocity = Velocity(),
        spin: Angle = 0.degrees,
    ) : MovingThing(position, velocity, spin) {
        fun applyWrap(world: World) = wrap(world)
        fun applyBounce(world: World) = bounce(world)
    }

    @Test
    fun movingThingActUpdatesLocationAndRotation() = runTest {
        val thing = TestMovingThing(
            position = Position(location = Location(1f, 2f, 3f)),
            velocity = Velocity(4f, -2f, 1f),
            spin = 90.degrees,
        )
        thing.act(1.seconds)
        assertFloatEquals(5f, thing.position.location.x)
        assertFloatEquals(0f, thing.position.location.y)
        assertFloatEquals(4f, thing.position.location.z)
        assertFloatEquals(90f, thing.position.rotation.r.degrees)
    }

    @Test
    fun movingThingWrapCrossesWorldBoundaries() {
        val world = World(left = -10f, top = -10f, right = 10f, bottom = 10f)
        val thing = TestMovingThing(position = Position(location = Location(-11f, 12f, 0f)))
        thing.applyWrap(world)
        assertFloatEquals(9f, thing.position.location.x)
        assertFloatEquals(-8f, thing.position.location.y)
    }

    @Test
    fun movingThingBounceReflectsOnlyCollidingAxes() {
        val world = World(left = -10f, top = -10f, right = 10f, bottom = 10f)
        val thing = TestMovingThing(
            position = Position(location = Location(-12f, 3f, 0f)),
            velocity = Velocity(-5f, 7f, 2f),
        )
        thing.applyBounce(world)
        assertFloatEquals(-8f, thing.position.location.x)
        assertFloatEquals(3f, thing.position.location.y)
        assertFloatEquals(0f, thing.position.location.z)
        // X velocity reflected, Y and Z velocities preserved!
        assertFloatEquals(5f, thing.velocity.xs)
        assertFloatEquals(7f, thing.velocity.ys)
        assertFloatEquals(2f, thing.velocity.zs)
    }

    @Test
    fun sceneManagesEntitiesAndAssets() = runTest {
        val t1 = TestMovingThing()
        val t2 = TestMovingThing()
        val t3 = TestMovingThing()

        val scene = SceneScope().run {
            add(t1)
            add(t2)
            build()
        }
        assertEquals(listOf(t1, t2), scene.things)

        scene.remove(t1)
        assertEquals(listOf(t2), scene.things)

        val nextScene = SceneScope().run {
            add(t1)
            add(t3)
            build()
        }
        scene.replaceAll(nextScene)
        assertEquals(listOf(t1, t3), scene.things)

        scene.release()
        assertTrue(scene.things.isEmpty())
    }

    @Test
    fun fastForEachIteratesInOrder() {
        val list = listOf("a", "b", "c")
        val collected = mutableListOf<String>()
        list.fastForEach { collected.add(it) }
        assertEquals(list, collected)

        val indexed = mutableListOf<Pair<Int, String>>()
        list.fastForEachIndexed { i, s -> indexed.add(i to s) }
        assertEquals(listOf(0 to "a", 1 to "b", 2 to "c"), indexed)

        val array = arrayOf(10, 20, 30)
        var sum = 0
        array.fastForEachIndexed { i, v -> sum += i * v }
        assertEquals(0 * 10 + 1 * 20 + 2 * 30, sum)
    }

    @Test
    fun sourceParsesSchemesCorrectly() {
        val asset1 = "asset:click.wav".asSource
        assertIs<SourceAsset>(asset1)
        assertEquals("click.wav", asset1.name)

        val asset2 = "assets://sprite.png".asSource
        assertIs<SourceAsset>(asset2)
        assertEquals("sprite.png", asset2.name)

        val resource = "resource:town/tiles.png".asSource
        assertIs<SourceUrl>(resource)
        assertTrue(resource.url.toString().contains("town/tiles.png"))

        val file = "/tmp/sample.wav".asSource
        assertIs<SourceFile>(file)

        val url = "https://example.com/image.png".asSource
        assertIs<SourceURL>(url)

        val resId = 42.asSource
        assertIs<SourceRes>(resId)
        assertEquals(42, resId.id)
    }
}
