package gemini

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
