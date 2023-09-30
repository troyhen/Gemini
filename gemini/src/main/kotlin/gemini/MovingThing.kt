package gemini

import androidx.compose.ui.geometry.Size
import kotlin.time.Duration

open class MovingThing(
    position: Position = Position(),
    val velocity: Velocity = Velocity(),
    val spin: Angle = Angle(),
) : Thing(position), Actor {

    override suspend fun act(elapsed: Duration) {
        val seconds = elapsed.inSeconds
        position.location.x += velocity.x * seconds
        position.location.y += velocity.y * seconds
        position.rotation.r += spin * seconds
    }

    fun bounce(spaceSize: Size = Stage.screenSize) {
        if (position.location.x < 0) {
            position.location.x = -position.location.x
            velocity.x = -velocity.x
        }
        if (position.location.y < 0) {
            position.location.y -= position.location.y
            velocity.y = -velocity.y
        }
        if (position.location.x > spaceSize.width) {
            position.location.x = spaceSize.width * 2 - position.location.x
            velocity.x = -velocity.x
        }
        if (position.location.y > spaceSize.height) {
            position.location.y = spaceSize.height * 2 - position.location.y
            velocity.y = -velocity.y
        }
    }

    fun wrap(spaceSize: Size = Stage.screenSize) {
        if (position.location.x > spaceSize.width * 2 || position.location.x < -spaceSize.width) position.location.x %= spaceSize.width
        if (position.location.y > spaceSize.height * 2 || position.location.y < -spaceSize.height) position.location.y %= spaceSize.height
        if (position.location.x < 0) position.location.x += spaceSize.width
        else if (position.location.x >= spaceSize.width) position.location.x -= spaceSize.width
        if (position.location.y < 0) position.location.y += spaceSize.height
        else if (position.location.y >= spaceSize.height) position.location.y -= spaceSize.height
    }
}
