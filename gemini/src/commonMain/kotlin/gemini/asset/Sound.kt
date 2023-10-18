package gemini.asset

expect class Sound() : Asset {
    override val isLoaded: Boolean
    override suspend fun load(source: Source)
    fun play(times: Int = 1)
    override fun release()
    fun stop()
}