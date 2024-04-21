import core.Context
import core.model.algebra.Line3
import core.model.algebra.Point3
import core.model.objects.WireframeObject
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min

class WireframePanel(private val context: Context) : JPanel(), MouseWheelListener {
    private var angleX = 0.0
    private var angleY = 0.0
    private var lastX = 0
    private var lastY = 0
    private var scaleCoefficient = 100.0

    init {
        background = Color.BLACK
        addMouseWheelListener(this)
        setupMouseListeners()
    }

    fun resetAngles() {
        angleX = 0.0
        angleY = 0.0
        repaint()
    }

    private fun setupMouseListeners() {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                lastX = e.x
                lastY = e.y
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                val dx = e.x - lastX
                val dy = e.y - lastY

                angleX = (angleX + dy.toDouble() / 500).mod(2 * Math.PI)
                angleY = (angleY + dx.toDouble() / 500).mod(2 * Math.PI)

                lastX = e.x
                lastY = e.y
                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        drawWireframe(g2)
        drawAxes(g2)
    }

    private fun drawAxes(g2d: Graphics2D) {
        val scale = 50
        val oX = Point3(scale, 0, 0).toRotated(0.0, angleX, angleY)
        val oY = Point3(0, scale, 0).toRotated(0.0, angleX, angleY)
        val oZ = Point3(0, 0, scale).toRotated(0.0, angleX, angleY)

        drawAxe(g2d, oX, Color.blue)
        drawAxe(g2d, oY, Color.red)
        drawAxe(g2d, oZ, Color.green)
    }

    private fun drawAxe(g2d: Graphics2D, axe: Point3, color: Color) {
        val centerX = width / 2
        val centerY = height / 2
        val (x, y) = toScreenCoords(centerX, centerY, axe)
        g2d.color = color
        g2d.drawLine(centerX, centerY, x, y)
    }

    private fun drawWireframe(g2: Graphics2D) {
        val centerX = width / 2
        val centerY = height / 2
        if (context.wireframeObject == null) {
            return
        }
        val transformedObject = context.wireframeObject!!
            .toRotated(0.0, angleX, angleY)
            .toScaled(scaleCoefficient)

        g2.color = Color.LIGHT_GRAY

        val lines: List<Line3> = transformedObject.lines
        lines.forEach { line ->
            val (x1, y1) = toScreenCoords(centerX, centerY, line.start)
            val (x2, y2) = toScreenCoords(centerX, centerY, line.end)
            val colorAlpha = calculateColorAlpha(line, transformedObject)
            g2.color = colorAlpha
            g2.stroke = BasicStroke(1f)
            g2.drawLine(x1, y1, x2, y2)
        }
    }

    private fun toScreenCoords(centerX: Int, centerY: Int, point: Point3): Pair<Int, Int> {
        return Pair(centerX + point.y.toInt(), centerY - point.z.toInt())
    }

    private fun calculateColorAlpha(line: Line3, obj: WireframeObject): Color {
        val minHeight = (line.start.x + line.end.x) / 2 - obj.minHeight
        val alpha = max(64, min(255, (255.0 * minHeight / (obj.maxHeight - obj.minHeight)).toInt()))
        return Color(250, 240, 230, alpha)
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        scaleCoefficient *= if (e.wheelRotation < 0) 1.01 else 0.99
        repaint()
    }

    fun normalize() {
        scaleCoefficient = min(height, width) / 8.0
        repaint()
    }
}