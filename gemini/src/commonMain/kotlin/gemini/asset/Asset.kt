package gemini.asset

abstract class Asset {
    abstract val isLoaded: Boolean
    abstract suspend fun load(source: Source)
    abstract fun release()
}
