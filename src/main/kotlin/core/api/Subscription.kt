package core.api

import api.OnUpdateListener
import core.model.engine.Camera
import core.model.engine.Scene
import core.model.engine.SceneObject

data class Subscription<T : SceneObject<T>>(
    val listener: OnUpdateListener,
    val scene: Scene<T>,
    val camera: Camera
)
