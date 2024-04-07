package core.model.engine

open class Scene<T : SceneObject<T>> {
    val objects = mutableMapOf<String, T>()
}