package api

import model.algebra.Point3
import model.engine.Scene
import model.engine.SceneObject
import model.engine.Transformation

/**
 * Интерфейс, определяющий контроллер для управления объектами сцены.
 *
 * @param S тип сцены, к которой относятся объекты, управляемые контроллером
 */
interface Controller<S : Scene> {
    /**
     * Добавляет объект на сцену и возвращает его идентификатор.
     *
     * @param sceneObject объект сцены, который необходимо добавить
     * @param position    позиция, на которой следует разместить объект
     * @return идентификатор добавленного объекта
     */
    fun <M : SceneObject<S>> add(sceneObject: M, position: Point3): String

    /**
     * Удаляет объект с указанным идентификатором со сцены.
     *
     * @param id идентификатор объекта, который необходимо удалить
     */
    fun <M : SceneObject<S>> remove(id: String)

    /**
     * Применяет преобразование к объекту с указанным идентификатором.
     *
     * @param id             идентификатор объекта
     * @param transformation функция, которая применяет преобразование к объекту
     */
    fun transform(id: String, transformation: (Transformation) -> Unit)
}