package gemini

import kotlin.time.Duration

open class MovingThing(position: Position = Position()) : Thing(position) {
    val velocity: Velocity = Velocity()

    fun act(elapsed: Duration) {
        val seconds = elapsed.inSeconds
        position.location.x += velocity.x * seconds
        position.location.y += velocity.y * seconds
    }
}
