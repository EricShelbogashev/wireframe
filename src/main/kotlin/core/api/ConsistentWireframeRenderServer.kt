package core.api

import api.OnUpdateListener
import api.RenderServer
import core.model.algebra.Line3
import core.model.engine.Camera
import core.model.engine.Scene
import core.model.engine.SceneObject
import core.model.objects.WireframeObject
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min

object ConsistentWireframeRenderServer : RenderServer {
    private val subscribers = mutableMapOf<Scene<WireframeObject>, OnUpdateListener>()
    private val tasks = ConcurrentLinkedQueue<() -> Pair<Int, BufferedImage>>()

    fun subscribe(
        scene: Scene<WireframeObject>,
        camera: Camera,
        onUpdate: OnUpdateListener,
    ): Controller<WireframeObject> {
        subscribers[scene] = onUpdate
        return object : Controller<WireframeObject> {
            val centerX = camera.width / 2
            val centerY = camera.height / 2

            private var sizeFactor = 1.0
            private var cameraAngleX = 0.0
            private var cameraAngleY = 0.0
            private var cameraAngleZ = 0.0

            private var fov = Math.PI / 2 // Угол обзора по умолчанию
            private var aspect = 1.0 // Соотношение сторон по умолчанию
            private var near = 1.0 // Ближняя плоскость отсечения
            private var far = .001 // Дальняя плоскость отсечения

            private fun updateScreen() {
                val result = BufferedImage(camera.width, camera.height, BufferedImage.TYPE_INT_RGB)

                scene.objects.values.first().scale(0.9)
                val g: Graphics2D = result.createGraphics()
                g.color = Color.BLACK
                g.fillRect(0, 0, result.width, result.height)

                scene.objects.forEach { (id, obj) -> drawObject(id, obj, g) }
                g.dispose()
                onUpdate(result)
            }

            private fun drawObject(id: String, wireframeObject: WireframeObject, g2: Graphics2D) {
                val transformedObject: WireframeObject = wireframeObject
                    .toRotated(.0, cameraAngleX, cameraAngleY) // Вращение
                    .toScaled(sizeFactor) // Масштабирование
                    .toAppliedPerspective(fov, aspect, near, far)
                g2.paint = Color.LIGHT_GRAY
                g2.drawOval(centerX, centerY, 1, 1)
                g2.drawOval(centerX, centerY, 0, 0)
                g2.drawOval(centerX, centerY, 2, 2)

                val lines: List<Line3> =
                    transformedObject.lines // Пример использования, предполагает, что linearize возвращает актуальные линии для отрисовки
                for (line in lines) {
                    // Пересчитываем координаты с учетом центра экрана как начала координат
                    val x1 = centerX + line.start.y.toInt()
                    val y1 = centerY - line.start.z.toInt() // Y инвертирован, т.к. в Swing Y увеличивается сверху вниз
                    val x2 = centerX + line.end.y.toInt()
                    val y2 = centerY - line.end.z.toInt() // Аналогично инвертируем Y для конечной точки
                    val minHeight = (line.start.x + line.end.x) / 2 - transformedObject.minHeight
                    val alpha = max(
                        32,
                        min(
                            255,
                            (255.0 * minHeight / (transformedObject.maxHeight - transformedObject.minHeight)).toInt()
                        )
                    )
                    g2.paint = arrayOf(
                        Color(250, 240, 230, alpha),
                        Color(255, 255, 240, alpha),
                        Color(255, 250, 240, alpha)
                    ).random()
                    g2.stroke = BasicStroke(1f)
                    g2.drawLine(x1, y1, x2, y2)
                }
            }

            override fun update(action: Camera.() -> Unit) {
                // TODO вынести логику обновления камеры, а здесь только вызывать
                action(camera)
                updateScreen()
            }

            override fun update(id: String, transformation: SceneObject<WireframeObject>.() -> Unit) {
                val sceneObject = scene.objects[id] ?: throw NoSuchElementException("запрашиваемый объект не найден")
                // TODO вынести логику обновления в сцену, а здесь только вызывать
                transformation(sceneObject)
                updateScreen()
            }
        }
    }

    override fun unsubscribe(listener: OnUpdateListener) {
        val removed = subscribers.entries.removeIf { it.value == listener }
        if (!removed) {
            throw NoSuchElementException("Listener not found")
        }
    }
}