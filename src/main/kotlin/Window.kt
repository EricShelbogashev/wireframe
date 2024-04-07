import com.formdev.flatlaf.FlatLightLaf
import core.Context
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.system.exitProcess

class Window : JFrame() {
    private val context = Context()

    private lateinit var fovTextField: JTextField
    private lateinit var aspectTextField: JTextField
    private lateinit var nearTextField: JTextField
    private lateinit var farTextField: JTextField

    private lateinit var fovSlider: JSlider
    private lateinit var aspectSlider: JSlider
    private lateinit var nearSlider: JSlider
    private lateinit var farSlider: JSlider

    init {
        setupUI()
    }

    private fun setupUI() {
        layout = BorderLayout()
        createPreferencesPanel()
        createMenuBar()
        title = "Просмотр проволочной модели"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1200, 800)
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun createPreferencesPanel() {
        val preferences = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.lightGray
        }
        val renderView = WireframePanel(context)
        setupPerspectiveSliders(renderView)
        setupButtons(preferences, renderView)
        setupTextFields(preferences)

        val rootPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, preferences, renderView)
        add(rootPane)
    }

    private fun setupPerspectiveSliders(panel: JPanel) {
        fovSlider = createSlider(0, 180, Math.toDegrees(context.fieldOfView).toInt()).apply {
            addChangeListener {
                context.fieldOfView = Math.toRadians(value.toDouble())
                fovTextField.text = value.toString()
                panel.repaint()
            }
        }
        aspectSlider = createSlider(1, 200, (context.aspectRatio * 100).toInt()).apply {
            addChangeListener {
                context.aspectRatio = value / 100.0
                aspectTextField.text = (value / 100.0).toString()
                panel.repaint()
            }
        }
        nearSlider = createSlider(0, 100, context.nearClip.toInt()).apply {
            addChangeListener {
                context.nearClip = value.toDouble() / 100
                nearTextField.text = (value.toDouble() / 100).toString()
                panel.repaint()
            }
        }
        farSlider = createSlider(0, 100, (context.farClip).toInt()).apply {
            addChangeListener {
                context.farClip = value.toDouble() / 10000 / 4
                farTextField.text = (value.toDouble() / 100).toString()
                panel.repaint()
            }
        }
    }

    private fun createSlider(min: Int, max: Int, value: Int) = JSlider(JSlider.HORIZONTAL, min, max, value)

    private fun setupButtons(preferences: JPanel, renderView: WireframePanel) {
        val resetButton = createButton("Reset Angles") {
            renderView.resetAngles()
        }
        val normalizeButton = createButton("Normalize") {
            renderView.normalize()
        }
        preferences.add(resetButton)
        preferences.add(Box.createVerticalStrut(10))
        preferences.add(normalizeButton)
    }

    private fun createButton(text: String, action: () -> Unit): JButton {
        return JButton(text).apply {
            addActionListener {
                action()
            }
            alignmentX = Component.CENTER_ALIGNMENT
        }
    }

    private fun setupTextFields(preferences: JPanel) {
        fovTextField = createTextField(Math.toDegrees(context.fieldOfView).toInt().toString())
        aspectTextField = createTextField(context.aspectRatio.toString())
        nearTextField = createTextField(context.nearClip.toString())
        farTextField = createTextField(context.farClip.toString())

        preferences.apply {
            add(createLabel("Field of View:"))
            add(fovTextField)
            add(fovSlider)
            add(createLabel("Aspect Ratio:"))
            add(aspectTextField)
            add(aspectSlider)
            add(createLabel("Near Clip:"))
            add(nearTextField)
            add(nearSlider)
            add(createLabel("Far Clip:"))
            add(farTextField)
            add(farSlider)
        }
    }

    private fun createLabel(text: String) = JLabel(text)

    private fun createTextField(text: String) = JTextField(text).apply {
        isEditable = false
    }

    private fun createMenuBar() {
        val menuBar = JMenuBar()
        val fileMenu = JMenu("Файл")
        val exitMenuItem = JMenuItem("Выйти")
        val openWireframeEditor = JMenuItem("Редактировать")

        exitMenuItem.addActionListener {
            exitProcess(0)
        }
        openWireframeEditor.addActionListener {
            initWireframeEditor()
        }
        fileMenu.add(openWireframeEditor)
        fileMenu.add(exitMenuItem)
        menuBar.add(fileMenu)
        jMenuBar = menuBar
    }

    private fun initWireframeEditor() {
        val jFrame = JFrame("Редактирование проволочной модели")
        createMenuBar(jFrame)
        val editWindow = createEditWindow()
        jFrame.add(editWindow)
        setupFrame(jFrame)
    }

    private fun createMenuBar(frame: JFrame) {
        val menuBar = JMenuBar()
        val fileMenu = JMenu("File")
        val exitMenuItem = JMenuItem("Exit")

        exitMenuItem.addActionListener {
            exitProcess(0)
        }
        fileMenu.add(exitMenuItem)
        menuBar.add(fileMenu)
        frame.jMenuBar = menuBar
    }

    private fun createEditWindow(): JSplitPane {
        val wireframeEditPanel = WireframeEditPanel(context)
        val editPanel = createEditPanel(wireframeEditPanel)
        return JSplitPane(JSplitPane.VERTICAL_SPLIT, wireframeEditPanel, editPanel).apply {
            resizeWeight = 1.0
            dividerSize = 5
        }
    }

    private fun createEditPanel(wireframeEditPanel: WireframeEditPanel): JPanel {
        val panel = JPanel().apply {
            layout = GridLayout(6, 2)
        }
        val sliders = listOf(
            JSlider(JSlider.HORIZONTAL, 4, 10, context.k),
            JSlider(JSlider.HORIZONTAL, 1, 10, context.n),
            JSlider(JSlider.HORIZONTAL, 2, 10, context.m),
            JSlider(JSlider.HORIZONTAL, 1, 20, context.m1),
            JSlider(JSlider.HORIZONTAL, 1, 50, context.gridStep)
        )
        val textFields = listOf(
            JTextField(context.k.toString()),
            JTextField(context.n.toString()),
            JTextField(context.m.toString()),
            JTextField(context.m1.toString()),
            JTextField(context.gridStep.toString())
        )

        sliders.zip(textFields).forEachIndexed { index, (slider, textField) ->
            panel.add(
                JLabel(
                    when (index) {
                        0 -> "k:"
                        1 -> "n:"
                        2 -> "m:"
                        3 -> "m1:"
                        else -> "gridStep:"
                    }
                )
            )
            panel.add(slider)
            panel.add(textField)
            slider.addChangeListener {
                val value = (it.source as JSlider).value
                textField.text = value.toString()
                updateContextFromSlider(index, value)
                wireframeEditPanel.repaint()
            }
            textField.document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    updateContextFromTextField(textField.text.toIntOrNull(), index, sliders)
                }

                override fun removeUpdate(e: DocumentEvent) {
                    updateContextFromTextField(textField.text.toIntOrNull(), index, sliders)
                }

                override fun changedUpdate(e: DocumentEvent) {
                    updateContextFromTextField(textField.text.toIntOrNull(), index, sliders)
                }
            })
        }
        return panel
    }

    private fun updateContextFromSlider(index: Int, value: Int) {
        when (index) {
            0 -> context.k = value
            1 -> context.n = value
            2 -> context.m = value
            3 -> context.m1 = value
            4 -> context.gridStep = value
        }
    }

    private fun updateContextFromTextField(value: Int?, index: Int, sliders: List<JSlider>) {
        value?.let {
            when (index) {
                0 -> context.k = it
                1 -> context.n = it
                2 -> context.m = it
                3 -> context.m1 = it
                4 -> context.gridStep = it
            }
            SwingUtilities.invokeLater {
                sliders[index].value = it
            }
        }
    }

    private fun setupFrame(frame: JFrame) {
        frame.setSize(800, 600)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("apple.awt.application.name", "ICGWireframe")
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            FlatLightLaf.setup()
            Window()
        }
    }
}
