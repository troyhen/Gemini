package gemini.engine


inline fun <E> Array<E>.fastForEach(from: Int = 0, block: (E) -> Unit) {
    for (index in from until size) {
        block(get(index))
    }
}

inline fun <E> Array<E>.fastForEachIndexed(block: (Int, E) -> Unit) {
    repeat(size) { index ->
        block(index, get(index))
    }
}

inline fun <E> List<E>.fastForEach(from: Int = 0, block: (E) -> Unit) {
    for (index in from until size) {
        block(get(index))
    }
}

inline fun <E> List<E>.fastForEachIndexed(block: (Int, E) -> Unit) {
    repeat(size) { index ->
        block(index, get(index))
    }
}
