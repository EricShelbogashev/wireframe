package core.api

import core.model.engine.Camera
import core.model.engine.SceneObject
import core.model.objects.WireframeObject

/**
 * Интерфейс, определяющий контроллер для управления объектами сцены.
 *
 * @param S тип сцены, к которой относятся объекты, управляемые контроллером
 */
interface Controller<T : SceneObject<T>> {
//    /**
//     * Добавляет объект на сцену и возвращает его идентификатор.
//     *
//     * @param sceneObject объект сцены, который необходимо добавить
//     * @param position    позиция, на которой следует разместить объект
//     * @return идентификатор добавленного объекта
//     */
//    fun add(sceneObject: T, position: Point3): String
//
//    /**
//     * Удаляет объект с указанным идентификатором со сцены.
//     *
//     * @param id идентификатор объекта, который необходимо удалить
//     */
//    fun remove(id: String)
//
//    /**
//     * Применяет преобразование к объекту с указанным идентификатором.
//     *
//     * @param id             идентификатор объекта
//     * @param transformation функция, которая применяет преобразование к объекту
//     */
//    fun transform(id: String, transformation: SceneObject<T>.() -> Unit)
//
//    fun rotateCamera(radX: Double, radY: Double, radZ: Double)
//
//    fun translateCamera(x: Double, y: Double, z: Double)

    fun update(action: Camera.() -> Unit)

    fun update(id: String, transformation: SceneObject<WireframeObject>.() -> Unit)
}