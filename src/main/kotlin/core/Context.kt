package core

import core.model.generator.BSplineCurve
import core.model.objects.WireframeObject

/**
 * Представляет контекст приложения.
 *
 * @property wireframeObject Объект проволочной модели, который будет отображаться в графическом интерфейсе.
 * @property k Число опорных точек (не менее 4).
 * @property n Число отрезков для каждого участка B-сплайна (≥ 1).
 * @property m Число образующих (≥ 2).
 * @property m1 Число отрезков по окружностям между соседними образующими (≥ 1).
 * @property gridStep Шаг сетки на графическом интерфейсе (по умолчанию 10).
 */
data class Context(
    var wireframeObject: WireframeObject? = null,
    var bSplineCurve: BSplineCurve? = null,
    var k: Int = 8,
    var n: Int = 8,
    var m: Int = 8,
    var m1: Int = 8,
    var gridStep: Int = 50,
    var fieldOfView: Double = Math.PI / 2,
    var aspectRatio: Double = 1.0,
    var nearClip: Double = 1.0,
    var farClip: Double = 0.001,
)
