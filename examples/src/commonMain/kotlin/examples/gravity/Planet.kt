package examples.gravity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import gemini.engine.*
import gemini.foundation.MovingThing
import gemini.geometry.*
import kotlin.concurrent.Volatile
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.TimeSource

class Planet(
    position: Position,
    velocity: Velocity,
    var mass: Float,
    var color: Color,
    private val system: PlanetSystem,
) : MovingThing(position, velocity), Collider {

    var radius: Float = position.space.width / 2f
        private set

    var isGrabbed: Boolean = false

    var isAbsorbed: Boolean = false
        private set

    override suspend fun act(elapsed: Duration) {
        if (isAbsorbed) return
        if (isGrabbed) {
            system.updateCamera()
            return
        }
        val dt = elapsed.seconds.coerceAtMost(MAX_DELTA_SECONDS)
        val x = position.location.x
        val y = position.location.y
        system.all().fastForEach { other ->
            if (other !== this && !other.isAbsorbed) {
                val dx = other.position.location.x - x
                val dy = other.position.location.y - y
                val minDist = radius + other.radius
                val distSq = (dx * dx + dy * dy).coerceAtLeast(minDist * minDist)
                val dist = sqrt(distSq)
                val accel = GRAVITY * other.mass / (distSq * dist)
                velocity.xs += dx * accel * dt
                velocity.ys += dy * accel * dt
            }
        }
        position.location.move(velocity.xs * dt, velocity.ys * dt)
        system.updateCamera()
    }

    override fun collideWith(collider: Collider): Boolean {
        if (isAbsorbed) return true
        return when (collider) {
            is Planet -> {
                if (!collider.isAbsorbed) {
                    val combinedRadius = radius + collider.radius
                    if (distanceSquared(
                            position.location,
                            collider.position.location
                        ) <= combinedRadius * combinedRadius
                    ) {
                        when {
                            isGrabbed -> absorb(collider)
                            collider.isGrabbed -> collider.absorb(this)
                            collider.mass > mass -> collider.absorb(this)
                            else -> absorb(collider)
                        }
                    }
                }
                true
            }

            else -> false
        }
    }

    private fun absorb(other: Planet) {
        if (isAbsorbed || other.isAbsorbed) return
        other.isAbsorbed = true
        val totalMass = mass + other.mass
        if (!isGrabbed) {
            position.location.x = (position.location.x * mass + other.position.location.x * other.mass) / totalMass
            position.location.y = (position.location.y * mass + other.position.location.y * other.mass) / totalMass
            velocity.xs = (velocity.xs * mass + other.velocity.xs * other.mass) / totalMass
            velocity.ys = (velocity.ys * mass + other.velocity.ys * other.mass) / totalMass
        }
        if (other.mass > mass) {
            color = other.color
        }
        mass = totalMass
        radius = (radius * radius * radius + other.radius * other.radius * other.radius).pow(1f / 3f)
        val diameter = radius * 2f
        position.space.set(diameter, diameter)
        system.remove(other)
        Stage.instance?.remove(other)
        system.updateCamera()
    }

    override fun DrawScope.draw() {
        if (isAbsorbed) return
        drawCircle(color, radius, position.location.offset)
    }

    fun isHit(offset: Offset): Boolean {
        if (isAbsorbed) return false
        val dx = position.location.x - offset.x
        val dy = position.location.y - offset.y
        val hitRadius = radius.coerceAtLeast(MIN_HIT_RADIUS)
        return dx * dx + dy * dy <= hitRadius * hitRadius
    }

    companion object {
        const val GRAVITY = 400f
        const val MIN_RADIUS = 15f
        const val MAX_RADIUS = 40f
        const val MIN_DENSITY = 0.5f
        const val MAX_DENSITY = 2.0f
        const val MIN_SPEED = 60f
        const val MAX_SPEED = 240f
        private const val MIN_HIT_RADIUS = 24f
        private const val MAX_DELTA_SECONDS = 0.05f
    }
}

class PlanetSystem {
    private val lock = Any()
    private val mutablePlanets = mutableListOf<Planet>()

    @Volatile
    private var snapshot: List<Planet>? = null

    val centerOfMass: Location = Location()
    private var attachedCamera: Camera? = null

    @Volatile
    private var pointerDown = false
    private var dragging: Planet? = null
    private var spawnedPlanet: Planet? = null
    private var pressStartOffset = Offset.Zero
    private var grabStartWorld = Offset.Zero

    private val sampleX = FloatArray(HISTORY_SIZE)
    private val sampleY = FloatArray(HISTORY_SIZE)
    private val sampleTime = arrayOfNulls<TimeSource.Monotonic.ValueTimeMark>(HISTORY_SIZE)
    private var sampleCount = 0

    fun all(): List<Planet> {
        return snapshot ?: synchronized(lock) {
            snapshot ?: mutablePlanets.toList().also {
                snapshot = it
            }
        }
    }

    fun add(planet: Planet) {
        synchronized(lock) {
            mutablePlanets.add(planet)
            snapshot = null
        }
    }

    fun remove(planet: Planet) {
        synchronized(lock) {
            mutablePlanets.remove(planet)
            snapshot = null
        }
    }

    fun clear() {
        synchronized(lock) {
            mutablePlanets.clear()
            snapshot = null
        }
        centerOfMass.set(0f, 0f, 0f)
        dragging = null
        spawnedPlanet = null
        pointerDown = false
        pressStartOffset = Offset.Zero
        grabStartWorld = Offset.Zero
        sampleCount = 0
    }

    fun updateCenterOfMass(): Location {
        val current = all()
        var totalMass = 0f
        var sumX = 0f
        var sumY = 0f
        var sumZ = 0f
        current.fastForEach { planet ->
            if (!planet.isAbsorbed) {
                val m = planet.mass
                totalMass += m
                sumX += planet.position.location.x * m
                sumY += planet.position.location.y * m
                sumZ += planet.position.location.z * m
            }
        }
        if (totalMass > 0f) {
            centerOfMass.set(sumX / totalMass, sumY / totalMass, sumZ / totalMass)
        }
        return centerOfMass
    }

    fun updateCamera(camera: Camera? = null) {
        if (camera != null) {
            attachedCamera = camera
        }
        val current = all()
        if (current.isEmpty()) return
        val com = updateCenterOfMass()
        if (pointerDown) return
        attachedCamera?.lookAt(com)
        Stage.camera.lookAt(com)
    }

    fun findHit(offset: Offset): Planet? {
        val current = all()
        var closest: Planet? = null
        var minDistanceSq = Float.MAX_VALUE
        current.fastForEach { planet ->
            if (planet.isHit(offset)) {
                val dx = planet.position.location.x - offset.x
                val dy = planet.position.location.y - offset.y
                val distSq = dx * dx + dy * dy
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq
                    closest = planet
                }
            }
        }
        return closest
    }

    fun drag(offset: Offset) {
        drag(offset, Stage.instance)
    }

    fun drag(offset: Offset, stage: SceneScope?) {
        val activeCamera = stage?.camera ?: attachedCamera ?: Stage.camera
        if (!pointerDown) {
            pointerDown = true
            pressStartOffset = offset
            resetSamples(offset)
            val worldOffset = activeCamera.toWorld(offset)
            grabStartWorld = worldOffset
            val hit = findHit(worldOffset)
            if (hit != null) {
                dragging = hit
                spawnedPlanet = null
                hit.isGrabbed = true
                hit.position.location.set(worldOffset.x, worldOffset.y)
                hit.velocity.set(0f, 0f)
                updateCamera(activeCamera)
            } else {
                spawnedPlanet = stage?.planet(this, Location(worldOffset.x, worldOffset.y))
                dragging = null
            }
        } else {
            recordSample(offset)
            if (dragging == null && spawnedPlanet != null) {
                val dx = offset.x - pressStartOffset.x
                val dy = offset.y - pressStartOffset.y
                val candidate = spawnedPlanet
                if (dx * dx + dy * dy > DRAG_THRESHOLD_SQ && candidate != null && !candidate.isAbsorbed) {
                    dragging = candidate
                    candidate.isGrabbed = true
                }
            }
            dragging?.let { planet ->
                val worldX = grabStartWorld.x + (offset.x - pressStartOffset.x)
                val worldY = grabStartWorld.y + (offset.y - pressStartOffset.y)
                planet.position.location.set(worldX, worldY)
                planet.velocity.set(computeFlingVelocity())
                updateCamera(activeCamera)
            }
        }
    }

    fun drop(offset: Offset) {
        dragging?.let { planet ->
            if (sampleCount > 0 && (offset.x != sampleX[sampleCount - 1] || offset.y != sampleY[sampleCount - 1])) {
                recordSample(offset)
            }
            val worldX = grabStartWorld.x + (offset.x - pressStartOffset.x)
            val worldY = grabStartWorld.y + (offset.y - pressStartOffset.y)
            planet.position.location.set(worldX, worldY)
            planet.velocity.set(computeFlingVelocity())
            planet.isGrabbed = false
        }
        dragging = null
        spawnedPlanet = null
        pointerDown = false
        sampleCount = 0
        updateCamera()
    }

    private fun resetSamples(offset: Offset) {
        sampleCount = 1
        sampleX[0] = offset.x
        sampleY[0] = offset.y
        sampleTime[0] = Stage.time.markNow()
    }

    private fun recordSample(offset: Offset) {
        val now = Stage.time.markNow()
        if (sampleCount > 0 && sampleX[sampleCount - 1] == offset.x && sampleY[sampleCount - 1] == offset.y) {
            return
        }
        if (sampleCount < HISTORY_SIZE) {
            sampleX[sampleCount] = offset.x
            sampleY[sampleCount] = offset.y
            sampleTime[sampleCount] = now
            sampleCount++
        } else {
            for (i in 1 until HISTORY_SIZE) {
                sampleX[i - 1] = sampleX[i]
                sampleY[i - 1] = sampleY[i]
                sampleTime[i - 1] = sampleTime[i]
            }
            sampleX[HISTORY_SIZE - 1] = offset.x
            sampleY[HISTORY_SIZE - 1] = offset.y
            sampleTime[HISTORY_SIZE - 1] = now
        }
    }

    private fun computeFlingVelocity(): Offset {
        if (sampleCount < 2) return Offset.Zero
        val lastIdx = sampleCount - 1
        val latestTime = sampleTime[lastIdx] ?: return Offset.Zero
        val now = Stage.time.markNow()
        if ((now - latestTime).seconds > STOP_THRESHOLD_SECONDS) {
            return Offset.Zero
        }
        var startIdx = lastIdx - 1
        for (i in lastIdx - 1 downTo 0) {
            val t = sampleTime[i] ?: break
            if ((latestTime - t).seconds <= FLING_WINDOW_SECONDS) {
                startIdx = i
            } else {
                break
            }
        }
        val startTime = sampleTime[startIdx] ?: return Offset.Zero
        val dt = (latestTime - startTime).seconds.coerceAtLeast(MIN_FLING_DT_SECONDS)
        var vx = (sampleX[lastIdx] - sampleX[startIdx]) / dt
        var vy = (sampleY[lastIdx] - sampleY[startIdx]) / dt
        val speedSq = vx * vx + vy * vy
        if (speedSq > MAX_FLING_SPEED * MAX_FLING_SPEED) {
            val scale = MAX_FLING_SPEED / sqrt(speedSq)
            vx *= scale
            vy *= scale
        }
        return Offset(vx, vy)
    }

    companion object {
        private const val HISTORY_SIZE = 10
        private const val DRAG_THRESHOLD_SQ = 100f
        private const val STOP_THRESHOLD_SECONDS = 0.15f
        private const val FLING_WINDOW_SECONDS = 0.1f
        private const val MIN_FLING_DT_SECONDS = 0.004f
        private const val MAX_FLING_SPEED = 2500f
    }
}

private val planetColors = listOf(
    Color.Cyan,
    Color.Magenta,
    Color.Yellow,
    Color.Green,
    Color.Red,
    Color.Blue,
    Color(0xFFFF9800),
    Color(0xFFAB47BC),
)

fun randomRadius(): Float = Planet.MIN_RADIUS + (Planet.MAX_RADIUS - Planet.MIN_RADIUS).random

fun randomMass(radius: Float): Float {
    val density = Planet.MIN_DENSITY + (Planet.MAX_DENSITY - Planet.MIN_DENSITY).random
    return density * radius * radius * radius
}

fun randomVelocity(): Velocity {
    val speed = Planet.MIN_SPEED + (Planet.MAX_SPEED - Planet.MIN_SPEED).random
    return Velocity(Offset(speed, 0f) rotate 360.degrees.random)
}

fun randomColor(exclude: Color? = null): Color {
    val candidates = if (exclude != null) planetColors.filter { it != exclude } else planetColors
    return candidates[candidates.size.random]
}

fun SceneScope.planet(
    system: PlanetSystem,
    location: Location = Location(),
    velocity: Velocity = randomVelocity(),
    radius: Float = randomRadius(),
    mass: Float = randomMass(radius),
    color: Color = randomColor(),
): Planet {
    val diameter = radius * 2f
    return Planet(
        position = Position(location = location, space = Space(diameter, diameter)),
        velocity = velocity,
        mass = mass,
        color = color,
        system = system,
    ).also { thing ->
        system.add(thing)
        add(thing)
        system.updateCamera(camera)
    }
}

fun SceneScope.orbitingPlanets(
    system: PlanetSystem,
    center: Offset = Offset(500f, 500f),
): Pair<Planet, Planet> {
    val radius1 = randomRadius()
    val radius2 = randomRadius()
    val mass1 = randomMass(radius1)
    val mass2 = randomMass(radius2)
    val totalMass = mass1 + mass2

    val minSeparation = (radius1 + radius2) * 3f
    val distance = minSeparation + 180f.random
    val angle = 360.degrees.random
    val direction = Offset(angle.cos(), angle.sin())
    val tangent = Offset(-direction.y, direction.x) * if (2.random == 0) 1f else -1f

    val orbitalSpeed = sqrt(Planet.GRAVITY * totalMass / distance) * (0.85f + 0.3f.random)

    val location1 = Location(center - direction * (distance * (mass2 / totalMass)))
    val location2 = Location(center + direction * (distance * (mass1 / totalMass)))

    val velocity1 = Velocity(tangent * (orbitalSpeed * (mass2 / totalMass)))
    val velocity2 = Velocity(-tangent * (orbitalSpeed * (mass1 / totalMass)))

    val color1 = randomColor()
    val color2 = randomColor(exclude = color1)

    val first = planet(system, location1, velocity1, radius1, mass1, color1)
    val second = planet(system, location2, velocity2, radius2, mass2, color2)
    return first to second
}
