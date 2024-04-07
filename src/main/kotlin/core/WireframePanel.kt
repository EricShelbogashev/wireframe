import model.algebra.Line3
import model.objects.WireframeObject
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSlider
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min


class WireframePanel(var wireframeObject: WireframeObject) : JPanel(), MouseWheelListener {
    private var angleX = 0.0
    private var angleY = 0.0
    private var lastX = 0
    private var lastY = 0
    private var coeff = 1.0

    private var fov = Math.PI / 2 // Угол обзора по умолчанию
    private var aspect = 1.0 // Соотношение сторон по умолчанию
    private var n = 1.0 // Ближняя плоскость отсечения
    private var f = .001 // Дальняя плоскость отсечения

    // Слайдеры для перспективы
    private var sliderFOV: JSlider? = null
    private var sliderAspect: JSlider? = null
    private var sliderNear: JSlider? = null
    private var sliderFar: JSlider? = null

    init {
        background = Color.BLACK
        addMouseWheelListener(this)
        initMouseListeners()
        initPerspectiveSliders()
    }

    private fun initPerspectiveSliders() {
        // Создание слайдеров
        sliderFOV = JSlider(JSlider.HORIZONTAL, 0, 180, Math.toDegrees(fov).toInt())
        sliderAspect = JSlider(JSlider.HORIZONTAL, 1, 200, (aspect * 100).toInt())
        sliderNear = JSlider(JSlider.HORIZONTAL, 0, 100, n.toInt())
        sliderFar = JSlider(JSlider.HORIZONTAL, 0, 10, f.toInt())

        // Добавление слушателей слайдеров
        sliderFOV!!.addChangeListener { e ->
            fov = Math.toRadians(sliderFOV!!.value.toDouble())
            repaint()
        }
        sliderAspect!!.addChangeListener { e ->
            aspect = sliderAspect!!.value / 100.0
            repaint()
        }
        sliderNear!!.addChangeListener { e ->
            n = sliderNear!!.value.toDouble() / 100
            repaint()
        }
        sliderFar!!.addChangeListener { e ->
            f = sliderFar!!.value.toDouble() / 10000
            repaint()
        }

        // Добавление слайдеров на панель
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(sliderFOV)
        add(sliderAspect)
        add(sliderNear)
        add(sliderFar)
    }


    private fun initMouseListeners() {
        val mouseAdapter: MouseAdapter = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                // Запоминаем начальные координаты при нажатии кнопки мыши
                lastX = e.x
                lastY = e.y
            }
        }

        val motionAdapter: MouseMotionAdapter = object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                // Вычисляем разницу между текущей и последней позицией
                val dx: Int = e.x - lastX
                val dy: Int = (e.y - lastY)

                // Обновляем углы вращения на основе движения мыши
                angleX += dy.toDouble() / 500
                angleX %= 2 * PI
                angleY += dx.toDouble() / 500
                angleY %= 2 * PI

                // Запоминаем текущую позицию как последнюю для следующего события
                lastX = e.x
                lastY = e.y

                repaint() // Перерисовываем объект с новыми углами
            }
        }

        addMouseListener(mouseAdapter)
        addMouseMotionListener(motionAdapter)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawWireframe(g as Graphics2D)
    }

    private fun drawWireframe(g2: Graphics2D) {
        val centerX = width / 2
        val centerY = height / 2

        val transformedObject: WireframeObject = wireframeObject
            .toRotated(.0, angleX, angleY) // Вращение
            .toScaled(coeff) // Масштабирование
            .toAppliedPerspective(fov, aspect, n, f)
        g2.paint = Color.LIGHT_GRAY
        g2.drawOval(centerX, centerY, 1, 1)
        g2.drawOval(centerX, centerY, 0, 0)
        g2.drawOval(centerX, centerY, 2, 2)

        val lines: List<Line3> =
            transformedObject.toLinearized(0.1) // Пример использования, предполагает, что linearize возвращает актуальные линии для отрисовки
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

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        if (e.wheelRotation < 0) {
            coeff += 0.01
        } else {
            coeff -= 0.01
        }
        repaint()
    }
}
