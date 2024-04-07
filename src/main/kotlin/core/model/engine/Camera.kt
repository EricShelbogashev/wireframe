package core.model.engine

import core.model.algebra.Point3
import kotlin.math.PI
import kotlin.math.tan

/**
 * Класс, представляющий камеру для трассировки.
 *
 * @property fov вертикальный угол обзора камеры в радианах.
 * @property aspect соотношение сторон камеры (ширина / высота).
 * @property near расстояние от камеры до ближней плоскости отсечения.
 * @property far расстояние от камеры до дальней плоскости отсечения.
 * @property position позиция камеры в трехмерном пространстве.
 * @property height высота видимой области.
 * @property width ширина видимой области.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Camera {
    // вертикальный угол обзора
    var fov: Double
    var aspect: Double
    var near: Double
    var far: Double
    var position: Point3
    var height: Int
    var width: Int

    /**
     * @throws IllegalArgumentException Если аргументы выходят за ограничения параметров.
     */
    constructor(fov: Double, aspect: Double, near: Double, far: Double, position: Point3) {
        require(near >= .0 && far > near) {
            "near=$near, far=$far - расстояние до ближайшей плоскости отсечение не должно быть отрицательным, расстояние до дальней плоскости отсечения не должно быть меньше, чем до ближней"
        }
        require(fov > 0 && fov < PI) { "fov=$fov - должен быть (0, PI)" }
        require(aspect > .0) { "соотношение сторон должно быть положительным" }
        this.fov = fov
        this.aspect = aspect
        this.near = near
        this.far = far
        this.position = position
        this.height = (tan(fov / 2) * near).toInt()
        this.width = (height * aspect).toInt()
        require(width > 0 && height > 0) {
            "fov=$fov, near=$near, aspect=$aspect, width=$width, height=$height - ширина и высота должны быть больше нуля"
        }
    }

    /**
     * @throws IllegalArgumentException Если аргументы выходят за ограничения параметров.
     */
    constructor(width: Int, height: Int, fov: Double, depth: Double, position: Point3) {
        require(width > 0 && height > 0) { "width=$width, height=$height - должны быть больше нуля" }
        require(fov > 0 && fov < PI) { "fov=$fov - должен быть (0, PI)" }
        require(depth > .0) { "depth=$depth - должна быть положительной" }
        this.fov = fov
        this.aspect = width.toDouble() / height
        this.near = height / (2 * tan(fov / 2))
        this.far = near + depth
        this.position = position
        this.height = height
        this.width = width
        require(aspect > .0) { "слишком большая высота, соотношение сторон 0" }
        require(near >= .0) { "fov=$fov, некорректный fov, расстояние до ближайшей плоскости отсечения отрицательно" }
    }
}