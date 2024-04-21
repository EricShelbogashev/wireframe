import core.Context
import core.model.algebra.Line3
import core.model.algebra.Point3
import core.model.generator.BSplineCurve
import core.model.generator.BSplineRotatingFigure
import core.model.objects.WireframeObject
import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import kotlin.math.sqrt

/**
 * Панель для рисования объектов с использованием B-сплайнов.
 *
 * @property context Контекст приложения.
 */
class WireframeEditPanel(private val context: Context, private val renderView: WireframePanel) : JPanel() {
    private val bSplineCurve = context.bSplineCurve ?: BSplineCurve()
    private val undoKeyStroke = if (System.getProperty("os.name").startsWith("Mac")) {
        KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK)
    } else {
        KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)
    }
    private var isDragging = false
    private var draggingPointIndex = -1
    private val pointDiameter = 16

    init {
        background = Color.BLACK
        // Добавляем слушателя мыши для добавления точек при клике
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val x = e.x.toDouble()
                val y = e.y.toDouble()
                for (i in context.controlPoints.indices) {
                    val p = context.controlPoints[i]
                    val distance =
                        sqrt((x - width / 2 - p.x) * (x - width / 2 - p.x) + (y - height / 2 - p.y) * (y - height / 2 - p.y))
                    if (distance <= pointDiameter) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            removePoint(i)
                            return
                        } else {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                isDragging = true
                                draggingPointIndex = i
                                return
                            }
                        }
                    }
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    addPoint(e.x.toDouble(), e.y.toDouble())
                }
                super.mousePressed(e)
            }

            override fun mouseReleased(e: MouseEvent) {
                isDragging = false
                draggingPointIndex = -1
                super.mouseReleased(e)
            }

        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (isDragging && draggingPointIndex != -1) {
                    updatePoint(
                        draggingPointIndex,
                        Point3(e.x.toDouble() - width / 2, e.y.toDouble() - height / 2, 0.0)
                    )
                    repaint()
                }
                super.mouseDragged(e)
            }
        })

        // Добавляем слушателя клавиатуры для обработки комбинации Command+Z или Ctrl+Z
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(undoKeyStroke, "undo")
        actionMap.put("undo", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                if (context.controlPoints.isNotEmpty()) {
                    removeLastPoint()
                    repaint()
                }
            }
        })
    }

    private fun updatePoint(index: Int, point: Point3) {
        context.controlPoints[index] = point
        bSplineCurve.update(index, point)
    }

    // Метод для добавления точки и перерисовки
    private fun addPoint(x: Double, y: Double) {
        // Добавляем точку в кривую B-сплайн
        bSplineCurve.add(Point3(x - width / 2, y - height / 2, 0.0))
        // Добавляем точку в список для отображения
        context.controlPoints.add(Point3(x - width / 2, y - height / 2, 0.0))
        // Перерисовываем панель
        repaint()
    }

    private fun removePoint(index: Int) {
        // Удаляем точку из кривой B-сплайн
        bSplineCurve.remove(index)
        // Удаляем точку из списка для отображения
        context.controlPoints.removeAt(index)
        // Перерисовываем панель
        repaint()
    }

    // Метод для удаления последней добавленной точки
    private fun removeLastPoint() {
        context.controlPoints.removeLast()
        bSplineCurve.removeLast()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawAxes(g)
        drawBSpline(g)
        drawPoints(g)
    }

    // Метод для рисования осей координат
    private fun drawAxes(g: Graphics) {
        g.color = Color.DARK_GRAY.darker().darker()
        val step = context.gridStep
        // Вертикальные линии
        for (x in width / 2 until width step step) {
            g.drawLine(x.toDouble(), 0.0, x.toDouble(), height.toDouble())
        }
        for (x in width / 2 downTo 0 step step) {
            g.drawLine(x.toDouble(), 0.0, x.toDouble(), height.toDouble())
        }

        // Горизонтальные линии
        for (y in height / 2 until height step step) {
            g.drawLine(0, y, width, y)
        }
        for (y in height / 2 downTo 0 step step) {
            g.drawLine(0, y, width, y)
        }

        // Оси координат
        g.color = Color.DARK_GRAY.brighter()
        g.drawLine(width / 2, 0, width / 2, height) // Вертикальная линия
        g.drawLine(0, height / 2, width, height / 2) // Горизонтальная линия
    }

    // Метод для рисования B-сплайна
    private fun drawBSpline(g: Graphics) {
        g.color = Color.WHITE
        val splineSegments: List<Line3> = bSplineCurve.toLinearized(context.n) // Выберите подходящий шаг
        for (segment in splineSegments) {
            val (x1, y1) = convertToScreen(segment.start, 1)
            val (x2, y2) = convertToScreen(segment.end, 1)
            g.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
        }
    }

    // Метод для преобразования координат в экранные координаты
    private fun convertToScreen(p: Point3, indent: Int): Point3 {
        val centerX = width / 2
        val centerY = height / 2
        val x: Double = centerX + p.x * indent
        val y: Double = centerY + p.y * indent
        return Point3(x, y, 0.0)
    }

    // Метод для рисования добавленных точек
    private fun drawPoints(g: Graphics) {
        if (context.controlPoints.isEmpty()) return
        val list = context.controlPoints.map {
            Point3(it.x + width / 2, it.y + height / 2, 0.0)
        }
        var (lX, lY) = list.first()
        for ((x, y) in list) {
            g.drawLine(lX, lY, x, y)
            g.color = Color(2, 121, 253)
            g.fillCircle(x, y, pointDiameter)
            lX = x
            lY = y
        }
    }

    override fun repaint() {
        super.repaint()
        if (context == null) return
        context.bSplineCurve = bSplineCurve
        val rotatingFigure = BSplineRotatingFigure(bSplineCurve, context)
        val linearized = rotatingFigure.toLinearized(context.n)
        if (linearized.isEmpty()) return
        context.wireframeObject = WireframeObject(linearized).toNormalized()
        renderView.repaint()
    }
}

fun Graphics.drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
    drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
}

fun Graphics.fillCircle(x: Double, y: Double, diameter: Int) {
    val radius = diameter / 2.0
    val xCenter = (x - radius).toInt()
    val yCenter = (y - radius).toInt()
    drawOval(xCenter, yCenter, diameter, diameter)
}
