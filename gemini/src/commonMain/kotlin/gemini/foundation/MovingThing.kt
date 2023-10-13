package gemini.foundation

import gemini.engine.Stage
import gemini.geometry.*
import kotlin.time.Duration

open class MovingThing(
    position: Position = Position(),
    val velocity: Velocity = Velocity(),
    var spin: Angle = Angle(),
) : Thing(position) {

    override suspend fun act(elapsed: Duration) {
        val seconds = elapsed.seconds
        position.location.x += velocity.xs * seconds
        position.location.y += velocity.ys * seconds
        position.rotation.r += spin * seconds
    }

    fun bounce(world: World = Stage.visible) {
        if (position.location.x < world.left) {
            position.location.x = -position.location.x
            velocity.xs = -velocity.xs
        }
        if (position.location.y < world.top) {
            position.location.y -= position.location.y
            velocity.ys = -velocity.ys
        }
        if (position.location.x > world.right) {
            position.location.x = world.right * 2 - position.location.x
            velocity.xs = -velocity.xs
        }
        if (position.location.y > world.bottom) {
            position.location.y = world.bottom * 2 - position.location.y
            velocity.ys = -velocity.ys
        }
    }

    fun wrap(world: World = Stage.visible) {
        if (position.location.x > world.right || position.location.x < world.left) position.location.x = (position.location.x - world.left) % world.width + world.left
        if (position.location.x < world.left) position.location.x += world.width
        if (position.location.y > world.bottom || position.location.y < world.top) position.location.y = (position.location.y - world.top) % world.height + world.top
        if (position.location.y < world.top) position.location.y += world.height
    }
}
