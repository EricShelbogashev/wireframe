import model.algebra.Line3
import model.algebra.Point3
import model.generator.BSplineCurve
import model.generator.BSplineRotatingFigure
import model.objects.WireframeObject
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities


class BSplineApp : JFrame() {
    private var panel1: WireframePanel
    private val bSplineCurve = BSplineCurve()
    private val drawingPanel: DrawingPanel = DrawingPanel()
    private val pointzzes = mutableListOf<Point3>()

    init {
        title = "B-Spline Demo"
        setSize(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        add(drawingPanel)
        initMouseListeners()

        //
        val curve = BSplineCurve()
        curve.add(Point3(0.0, 0.0, 0.0))
        curve.add(Point3(0.0, 200.0, 0.0))
        curve.add(Point3(200.0, 0.0, 0.0))
        curve.add(Point3(2.0, 200.0, 0.0))
        curve.add(Point3(333.0, 200.0, 0.0))
        curve.add(Point3(2.0, 333.0, 0.0))
        val figure = BSplineRotatingFigure(curve, 64)
        val wireframeObject = WireframeObject(figure.toLinearized(0.01))
        val frame1 = JFrame("Wireframe Viewer")
        panel1 = WireframePanel(wireframeObject)
        frame1.defaultCloseOperation = EXIT_ON_CLOSE
        frame1.add(panel1)
        frame1.setSize(800, 600)
        frame1.setLocationRelativeTo(null) // Располагаем окно по центру экрана
        frame1.isVisible = true
    }

    private fun initMouseListeners() {
        drawingPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)
                val y = e.y.toDouble()
                val x = e.x.toDouble()
                bSplineCurve.add(Point3(x - width / 2, y - height / 2, 0.0) / 30.0)
                pointzzes.add(Point3(x, y, 0.0))
                drawingPanel.repaint()
                val figure = BSplineRotatingFigure(bSplineCurve, 32)
                val wireframeObject = WireframeObject(figure.toLinearized(0.01))
                wireframeObject.scale(100.0)
                panel1.wireframeObject = wireframeObject
            }
        })
    }

    private inner class DrawingPanel : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.drawLine(width / 2, 0, width / 2, height) // Вертикальная линия
            g.drawLine(0, height / 2, width, height / 2) // Горизонтальная линия
            drawBSpline(g)
        }

        private fun drawBSpline(g: Graphics) {
            g.color = Color.RED
            val splineSegments: List<Line3> = bSplineCurve.toLinearized(0.005) // Выберите подходящий шаг
            for (segment in splineSegments) {
                val (x1, y1) = convertToScreen(segment.start, 30)
                val (x2, y2) = convertToScreen(segment.end, 30)
                g.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            }
            for ((x, y) in pointzzes) {
                g.color = Color.BLUE
                g.fillOval(x.toInt(), y.toInt(), 6, 6)
                g.color = Color.RED
            }
        }
    }

    private fun convertToScreen(p: Point3, indent: Int): Point3 {
        val centerX = width / 2
        val centerY = height / 2
        val x: Double = centerX + p.x * indent
        val y: Double = centerY + p.y * indent
        return Point3(x, y, 0.0)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                val app = BSplineApp()
                app.isVisible = true
            }
        }
    }
}
