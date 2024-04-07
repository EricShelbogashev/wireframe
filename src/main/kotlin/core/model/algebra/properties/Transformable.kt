package model.algebra.properties

interface Transformable<T> {
    fun scale(factor: Double)
    fun toScaled(factor: Double): T
    fun scale(factorX: Double, factorY: Double, factorZ: Double)
    fun toScaled(factorX: Double, factorY: Double, factorZ: Double): T
    fun rotate(radX: Double, radY: Double, radZ: Double)
    fun toRotated(radX: Double, radY: Double, radZ: Double): T

    /**
     * Применяет перспективное преобразование к текущему объекту.
     * Изменяет состояние текущего объекта.
     *
     * @param fov Угол обзора в радианах.
     * @param aspect Соотношение сторон области просмотра.
     * @param n Расстояние до ближней плоскости отсечения.
     * @param f Расстояние до дальней плоскости отсечения.
     */
    fun applyPerspective(fov: Double, aspect: Double, n: Double, f: Double)

    /**
     * Возвращает новый объект с применённым перспективным преобразованием.
     * Не изменяет состояние текущего объекта.
     *
     * @param fov Угол обзора в радианах.
     * @param aspect Соотношение сторон области просмотра.
     * @param n Расстояние до ближней плоскости отсечения.
     * @param f Расстояние до дальней плоскости отсечения.
     * @return Новый объект типа T с применённым перспективным преобразованием.
     */
    fun toAppliedPerspective(fov: Double, aspect: Double, n: Double, f: Double): T
}