package model.engine

interface Transformation {
    /**
     * Вращает объект с указанным идентификатором.
     *
     * @param id   идентификатор объекта
     * @param radX угол вращения по оси X в радианах
     * @param radY угол вращения по оси Y в радианах
     * @param radZ угол вращения по оси Z в радианах
     */
    fun rotate(id: String, radX: Double, radY: Float, radZ: Float)

    /**
     * Сдвигает объект с указанным идентификатором.
     *
     * @param id идентификатор объекта
     * @param x  сдвиг по оси X
     * @param y  сдвиг по оси Y
     * @param z  сдвиг по оси Z
     */
    fun translate(id: String, x: Double, y: Double, z: Double)

    /**
     * Масштабирует объект с указанным идентификатором по каждой оси.
     *
     * @param id идентификатор объекта
     * @param x  масштаб по оси X
     * @param y  масштаб по оси Y
     * @param z  масштаб по оси Z
     */
    fun scale(id: String, x: Double, y: Double, z: Double)

    /**
     * Масштабирует объект с указанным идентификатором на указанный множитель.
     *
     * @param id     идентификатор объекта
     * @param factor множитель масштабирования
     */
    fun scale(id: String, factor: Double)
}