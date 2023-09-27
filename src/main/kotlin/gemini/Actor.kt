package gemini

import kotlin.time.Duration

fun interface Actor {
    suspend fun act(elapsed: Duration)
}
