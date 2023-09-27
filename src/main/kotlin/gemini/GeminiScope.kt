package gemini

class GeminiScope {

    var camera = Camera()
    private val things = mutableListOf<Thing>()

    fun add(block: GeminiScope.() -> Thing) {
        things.add(block())
    }

    fun build(): Stage {
        val stage = Stage()
        stage.set(camera)
        stage.addAll(things)
        return stage
    }
}
