package gemini

import kotlin.time.Duration

inline fun <E> Array<E>.fastForEach(block: (E) -> Unit) {
    repeat(size) {
        block(get(it))
    }
}

inline fun <E> List<E>.fastForEach(block: (E) -> Unit) {
    repeat(size) {
        block(get(it))
    }
}

val Duration.inSeconds: Float get() = inWholeMicroseconds / 1e-6f
