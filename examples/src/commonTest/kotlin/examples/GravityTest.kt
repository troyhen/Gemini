package examples

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import examples.gravity.Planet
import examples.gravity.PlanetSystem
import examples.gravity.orbitingPlanets
import examples.gravity.planet
import gemini.engine.SceneScope
import gemini.geometry.Location
import gemini.geometry.Velocity
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class GravityTest {

    private fun assertFloatEquals(expected: Float, actual: Float, tolerance: Float = 1e-2f) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "Expected $expected (+/- $tolerance), but was $actual",
        )
    }

    @Test
    fun orbitingPlanetsCreatesBalancedTwoBodySystem() {
        val system = PlanetSystem()
        val scope = SceneScope()
        val (p1, p2) = scope.orbitingPlanets(system, center = Offset(500f, 500f))

        assertEquals(2, system.all().size)
        assertEquals(2, scope.things.size)
        assertTrue(p1.radius in Planet.MIN_RADIUS..Planet.MAX_RADIUS)
        assertTrue(p2.radius in Planet.MIN_RADIUS..Planet.MAX_RADIUS)
        assertTrue(p1.mass > 0f)
        assertTrue(p2.mass > 0f)

        // Total linear momentum of the initial binary orbit should be zero
        val px = p1.mass * p1.velocity.xs + p2.mass * p2.velocity.xs
        val py = p1.mass * p1.velocity.ys + p2.mass * p2.velocity.ys
        assertFloatEquals(0f, px, tolerance = 0.5f)
        assertFloatEquals(0f, py, tolerance = 0.5f)
    }

    @Test
    fun planetsAttractEachOtherUnderGravity() = runTest {
        val system = PlanetSystem()
        val scope = SceneScope()
        val p1 = scope.planet(
            system = system,
            location = Location(100f, 100f),
            velocity = Velocity(0f, 0f),
            radius = 20f,
            mass = 10_000f,
            color = Color.Cyan,
        )
        val p2 = scope.planet(
            system = system,
            location = Location(300f, 100f),
            velocity = Velocity(0f, 0f),
            radius = 20f,
            mass = 20_000f,
            color = Color.Magenta,
        )

        p1.act(16.milliseconds)
        p2.act(16.milliseconds)

        // p1 should accelerate toward p2 (+x) and p2 toward p1 (-x)
        assertTrue(p1.velocity.xs > 0f)
        assertTrue(p2.velocity.xs < 0f)
        assertTrue(p1.position.location.x > 100f)
        assertTrue(p2.position.location.x < 300f)
    }

    @Test
    fun collidingPlanetsMergeConservingMassVolumeAndMomentum() {
        val system = PlanetSystem()
        val scope = SceneScope()
        val p1 = scope.planet(
            system = system,
            location = Location(100f, 100f),
            velocity = Velocity(30f, 0f),
            radius = 20f,
            mass = 10_000f,
            color = Color.Cyan,
        )
        val p2 = scope.planet(
            system = system,
            location = Location(125f, 100f),
            velocity = Velocity(-15f, 30f),
            radius = 30f,
            mass = 20_000f,
            color = Color.Yellow,
        )

        val expectedRadius = (20f.pow(3) + 30f.pow(3)).pow(1f / 3f)
        val collided = p1.collideWith(p2)
        assertTrue(collided)

        // p2 has greater mass so p2 absorbs p1
        assertTrue(p1.isAbsorbed)
        assertFalse(p2.isAbsorbed)
        assertEquals(listOf(p2), system.all())
        assertFloatEquals(30_000f, p2.mass)
        assertFloatEquals(expectedRadius, p2.radius)
        assertFloatEquals(expectedRadius * 2f, p2.position.space.width)

        // Momentum conservation: (10000 * 30 + 20000 * -15) / 30000 = 0, (10000 * 0 + 20000 * 30) / 30000 = 20
        assertFloatEquals(0f, p2.velocity.xs)
        assertFloatEquals(20f, p2.velocity.ys)
    }

    @Test
    fun clickingEmptySpaceIntroducesNewRandomPlanet() {
        val system = PlanetSystem()
        val scope = SceneScope()
        scope.orbitingPlanets(system, center = Offset(500f, 500f))
        assertEquals(2, system.all().size)

        // Click far away in empty space
        val clickSpot = Offset(50f, 50f)
        system.drag(clickSpot, scope)
        system.drop(clickSpot)

        assertEquals(3, system.all().size)
        val added = system.all().last()
        assertFloatEquals(50f, added.position.location.x)
        assertFloatEquals(50f, added.position.location.y)
        assertTrue(added.velocity.xs != 0f || added.velocity.ys != 0f)
    }

    @Test
    fun grabbingAndFlingingPlanetChangesVelocity() {
        val system = PlanetSystem()
        val scope = SceneScope()
        val planet = scope.planet(
            system = system,
            location = Location(200f, 200f),
            velocity = Velocity(0f, 0f),
            radius = 30f,
            mass = 15_000f,
        )

        // Grab the planet and drag it in +x, -y direction
        system.drag(Offset(200f, 200f), scope)
        assertTrue(planet.isGrabbed)

        system.drag(Offset(260f, 170f), scope)
        system.drop(Offset(320f, 140f))

        assertFalse(planet.isGrabbed)
        assertFloatEquals(320f, planet.position.location.x)
        assertFloatEquals(140f, planet.position.location.y)
        assertTrue(planet.velocity.xs > 0f)
        assertTrue(planet.velocity.ys < 0f)
    }

    @Test
    fun cameraAlwaysLooksAtCenterOfMass() = runTest {
        val system = PlanetSystem()
        val scope = SceneScope()
        val p1 = scope.planet(
            system = system,
            location = Location(100f, 200f),
            velocity = Velocity(50f, 0f),
            radius = 20f,
            mass = 10_000f,
        )
        val p2 = scope.planet(
            system = system,
            location = Location(400f, 200f),
            velocity = Velocity(50f, 0f),
            radius = 30f,
            mass = 20_000f,
        )

        // Initial center of mass: (100 * 10000 + 400 * 20000) / 30000 = 300, y = 200
        assertFloatEquals(300f, system.centerOfMass.x)
        assertFloatEquals(200f, system.centerOfMass.y)
        assertFloatEquals(300f, scope.camera.target.x)
        assertFloatEquals(200f, scope.camera.target.y)

        // Advance simulation: both planets have net +x velocity so center of mass moves right
        p1.act(50.milliseconds)
        p2.act(50.milliseconds)

        val expectedComX = (p1.position.location.x * p1.mass + p2.position.location.x * p2.mass) / (p1.mass + p2.mass)
        val expectedComY = (p1.position.location.y * p1.mass + p2.position.location.y * p2.mass) / (p1.mass + p2.mass)
        assertTrue(expectedComX > 300f)
        assertFloatEquals(expectedComX, scope.camera.target.x)
        assertFloatEquals(expectedComY, scope.camera.target.y)

        // While mouse/touch is down, camera view stays fixed even as planets move or are dragged
        val p1X = p1.position.location.x
        val p1Y = p1.position.location.y
        system.drag(Offset(p1X, p1Y), scope)
        system.drag(Offset(p1X - 150f, p1Y + 90f), scope)
        p2.act(50.milliseconds)
        assertFloatEquals(expectedComX, scope.camera.target.x)
        assertFloatEquals(expectedComY, scope.camera.target.y)

        // Releasing mouse/touch updates camera target to the new center of mass
        system.drop(Offset(p1X - 150f, p1Y + 90f))

        val draggedComX = (p1.position.location.x * p1.mass + p2.position.location.x * p2.mass) / (p1.mass + p2.mass)
        val draggedComY = (p1.position.location.y * p1.mass + p2.position.location.y * p2.mass) / (p1.mass + p2.mass)
        assertFloatEquals(draggedComX, scope.camera.target.x)
        assertFloatEquals(draggedComY, scope.camera.target.y)
    }

    @Test
    fun pointerInputTranslatesScreenOffsetThroughShiftedCamera() {
        val system = PlanetSystem()
        val scope = SceneScope()
        // Simulate a 1000x800 screen viewport
        scope.camera.visible.set(0f, 1000f, 0f, 800f)

        scope.planet(
            system = system,
            location = Location(800f, 700f),
            velocity = Velocity(0f, 0f),
            radius = 25f,
            mass = 10_000f,
        )
        // Camera is now looking at (800, 700) on a 1000x800 viewport (center = 500, 400),
        // so visible is [300..1300] x [300..1100].
        assertFloatEquals(800f, scope.camera.target.x)
        assertFloatEquals(700f, scope.camera.target.y)

        // Clicking at screen (100, 100) should spawn a planet at world (400, 400)
        system.drag(Offset(100f, 100f), scope)
        system.drop(Offset(100f, 100f))

        assertEquals(2, system.all().size)
        val spawned = system.all().last()
        assertFloatEquals(400f, spawned.position.location.x)
        assertFloatEquals(400f, spawned.position.location.y)
    }
}
