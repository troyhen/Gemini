package gemini

import kotlin.time.Duration

interface Actor {
    suspend fun act(elapsed: Duration) = Unit
}
