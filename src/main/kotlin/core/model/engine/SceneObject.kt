package core.model.engine

import core.model.algebra.properties.Transformable

interface SceneObject<T> : Transformable<T> {
    /**
     * Сдвигает объект с указанным идентификатором.
     *
     * @param id идентификатор объекта
     * @param x  сдвиг по оси X
     * @param y  сдвиг по оси Y
     * @param z  сдвиг по оси Z
     */
    fun translate(x: Double, y: Double, z: Double)
}