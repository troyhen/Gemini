package gemini

open class Scene {
    val actors = mutableListOf<Actor>()
    val things = mutableListOf<Thing>()

    @Synchronized
    open fun add(actor: Actor) {
        actors.add(actor)
    }

    @Synchronized
    open fun add(thing: Thing) {
        things.add(thing)
    }

    @Synchronized
    open fun replaceAll(scene: Scene) {
        actors.clear()
        things.clear()
        actors.addAll(scene.actors)
        things.addAll(scene.things)
    }
}
