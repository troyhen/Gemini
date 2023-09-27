package gemini

import kotlin.time.Duration

typealias Actor = suspend (Duration) -> Unit
