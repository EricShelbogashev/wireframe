import com.fasterxml.jackson.databind.ObjectMapper
import com.formdev.flatlaf.FlatLightLaf
import core.Context
import java.awt.*
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.system.exitProcess

class Window : JFrame() {
    private var context = Context()
    private lateinit var renderView: WireframePanel
    private lateinit var shiftTextField: JTextField
    private lateinit var nearTextField: JTextField
    private lateinit var nearSlider: JSlider
    private lateinit var shiftSlider: JSlider

    init {
        setupUI()
    }

    private fun setupUI() {
        layout = BorderLayout()
        createPreferencesPanel()
        createMenuBar()
        title = "Просмотр проволочной модели"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1080, 480)
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun createPreferencesPanel() {
        renderView = WireframePanel(context)
        val preferences = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS) // Use BoxLayout for vertical layout
            background = Color.lightGray
            border = EmptyBorder(10, 10, 10, 10) // Add some padding
        }
        setupPerspectiveSliders(renderView)
        setupButtons(preferences, renderView)
        setupTextFields(preferences)

        val rootPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, preferences, renderView)
        add(rootPane)
    }


    private fun createTextFieldWithToolTip(text: String, tooltip: String): JTextField {
        val textField = createTextField(text)
        textField.toolTipText = "Allowed range: $tooltip"
        textField.background = Color.WHITE // Set background color to white
        textField.foreground = Color.BLACK // Set text color to black
        textField.border = BorderFactory.createLineBorder(Color.BLACK) // Set border color to black
        return textField
    }

    private fun setupPerspectiveSliders(panel: JPanel) {
        nearSlider = createSlider(0, (context.shift * 10000).toInt(), (context.nearClip).toInt()).apply {
            addChangeListener {
                if (value.toDouble() / 10000 > context.shift) return@addChangeListener
                context.nearClip = value.toDouble() / 10000
                nearTextField.text = (value.toDouble() / 10000).toString()
                panel.repaint()
            }
        }
        shiftSlider = createSlider(0, 20 * 10000, (context.nearClip).toInt()).apply {
            addChangeListener {
                context.shift = value.toDouble() / 10000
                nearSlider.maximum = value
                shiftTextField.text = (value.toDouble() / 10000).toString()
                panel.repaint()
            }
        }
    }

    private fun createSlider(min: Int, max: Int, value: Int) = JSlider(JSlider.HORIZONTAL, min, max, value)

    private fun setupButtons(preferences: JPanel, renderView: WireframePanel) {
        val resetButton = createButton("Сбросить углы") {
            renderView.resetAngles()
        }
        val normalizeButton = createButton("Нормализовать") {
            renderView.normalize()
        }

        resetButton.maximumSize = Dimension(Integer.MAX_VALUE, resetButton.preferredSize.height)
        normalizeButton.maximumSize = Dimension(Integer.MAX_VALUE, normalizeButton.preferredSize.height)

        preferences.apply {
            add(resetButton)
            add(Box.createVerticalStrut(10))
            add(normalizeButton)
        }
    }

    private fun loadDataFromFile() {
        val fileChooser = JFileChooser()
        val returnValue = fileChooser.showSaveDialog(this)
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            val value = ObjectMapper().readValue(selectedFile, Context::class.java)
            context.wireframeObject = value.wireframeObject
            context.bSplineCurve = value.bSplineCurve
            context.n = value.n
            context.m = value.m
            context.m1 = value.m1
            context.gridStep = value.gridStep
            context.fieldOfView = value.fieldOfView
            context.aspectRatio = value.aspectRatio
            context.nearClip = value.nearClip
            context.shift = value.shift
            context.controlPoints = value.controlPoints
            initWireframeEditor()

        }
        repaint()
    }

    private fun saveDataToFile() {
        val json = ObjectMapper().writeValueAsString(context)
        val fileChooser = JFileChooser()
        val returnValue = fileChooser.showSaveDialog(this)
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            var selectedFile = fileChooser.selectedFile
            if (!selectedFile.name.lowercase(Locale.getDefault()).endsWith(".json")) {
                selectedFile = File(selectedFile.absolutePath + ".json")
            }
            selectedFile.writeText(json)
        }
    }

    private fun createButton(text: String, action: () -> Unit): JButton {
        val button = JButton(text).apply {
            addActionListener {
                action()
            }
            alignmentX = Component.CENTER_ALIGNMENT
            isFocusPainted = false
            isBorderPainted = true
        }
        return button
    }

    private fun setupTextFields(preferences: JPanel) {
        nearTextField = createTextFieldWithToolTip(context.nearClip.toString(), "0-3000").apply {
            document.addDocumentListener(createDocumentListener(nearSlider))
            preferredSize = Dimension(100, 25) // Set preferred size for the text field
            background = Color.white // Set background color to dark gray
            foreground = Color.BLACK // Set text color to white
            caretColor = Color.BLACK // Set caret color to white
            border = BorderFactory.createLineBorder(Color.WHITE) // Set border color to black
            maximumSize = Dimension(Short.MAX_VALUE.toInt(), 15)
        }
        shiftTextField = createTextFieldWithToolTip(context.nearClip.toString(), "0-3000").apply {
            document.addDocumentListener(createDocumentListener(nearSlider))
            preferredSize = Dimension(100, 25) // Set preferred size for the text field
            background = Color.white // Set background color to dark gray
            foreground = Color.BLACK // Set text color to white
            caretColor = Color.BLACK // Set caret color to white
            border = BorderFactory.createLineBorder(Color.WHITE) // Set border color to black
            maximumSize = Dimension(Short.MAX_VALUE.toInt(), 15)
        }

        val nearPanel = JPanel(BorderLayout()).apply {
            add(createLabel("Near Clip:"), BorderLayout.WEST)
            add(nearTextField, BorderLayout.CENTER)
            add(nearSlider, BorderLayout.SOUTH)
            background = Color.white // Set background color to dark gray
            border = BorderFactory.createEmptyBorder(5, 0, 5, 0) // Add vertical spacing
            maximumSize = Dimension(Short.MAX_VALUE.toInt(), 64)
        }
        val shiftPanel = JPanel(BorderLayout()).apply {
            add(createLabel("Shift:"), BorderLayout.WEST)
            add(shiftTextField, BorderLayout.CENTER)
            add(shiftSlider, BorderLayout.SOUTH)
            background = Color.white // Set background color to dark gray
            border = BorderFactory.createEmptyBorder(5, 0, 5, 0) // Add vertical spacing
            maximumSize = Dimension(Short.MAX_VALUE.toInt(), 64)
        }

        preferences.apply {
            add(nearPanel)
            add(shiftPanel)
            background = Color.white // Set background color to dark gray for the preferences panel
        }
    }

    private fun createDocumentListener(slider: JSlider): DocumentListener {
        return object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                updateSliderFromTextField(slider)
            }

            override fun removeUpdate(e: DocumentEvent) {
                updateSliderFromTextField(slider)
            }

            override fun changedUpdate(e: DocumentEvent) {
                updateSliderFromTextField(slider)
            }
        }
    }

    /**
     * Синхронизирует TextView и слайдеры. Отключен.
     */
    private fun updateSliderFromTextField(slider: JSlider) {
//        val textField = nearTextField.takeIf { slider == nearSlider }
//            ?: nearTextField.takeIf { slider == nearSlider } ?: return
//        val value = textField.text.toIntOrNull() ?: return
//        SwingUtilities.invokeLater {
//            slider.value = value
//        }
    }

    private fun createLabel(text: String) = JLabel(text)

    private fun createTextField(text: String): JTextField {
        class LimitedTextField(private val maxLength: Int, text: String) : JTextField(text) {
            init {
                background = Color.WHITE
                foreground = Color.BLACK
                border = BorderFactory.createLineBorder(Color.BLACK)
            }

            override fun getPreferredSize(): Dimension {
                val preferredSize = super.getPreferredSize()
                preferredSize.width = Math.min(preferredSize.width, maxLength)
                return preferredSize
            }
        }
        return LimitedTextField(32, text)
    }

    private fun createMenuBar() {
        val menuBar = JMenuBar()
        val fileMenu = JMenu("Файл")
        val openFile = JMenuItem("Открыть")
        val exitMenuItem = JMenuItem("Выйти")
        val openWireframeEditor = JMenuItem("Редактировать")
        val saveButton = JMenuItem("Сохранить как")
        val aboutAuthorMenuItem = createAboutAuthorMenuItem() // Добавляем пункт меню "Об авторе"

        exitMenuItem.addActionListener {
            exitProcess(0)
        }
        openWireframeEditor.addActionListener {
            initWireframeEditor()
        }
        saveButton.addActionListener {
            saveDataToFile()
        }
        openFile.addActionListener {
            loadDataFromFile()
        }
        fileMenu.add(openWireframeEditor)
        fileMenu.add(openFile)
        fileMenu.add(saveButton)
        fileMenu.add(aboutAuthorMenuItem)
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
        val wireframeEditPanel = WireframeEditPanel(context, renderView)
        val editPanel = createEditPanel(wireframeEditPanel)
        return JSplitPane(JSplitPane.VERTICAL_SPLIT, wireframeEditPanel, editPanel).apply {
            resizeWeight = 1.0
            dividerSize = 5
        }
    }

    private fun createAboutAuthorMenuItem(): JMenuItem {
        val aboutAuthorMenuItem = JMenuItem("Об авторе")
        aboutAuthorMenuItem.addActionListener {
            showAboutAuthorDialog()
        }
        return aboutAuthorMenuItem
    }

    private fun showAboutAuthorDialog() {
        val aboutAuthorMessage = "Автор: Эрик Шелбогашев 21209"
        JOptionPane.showMessageDialog(this, aboutAuthorMessage, "Об авторе", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun createEditPanel(wireframeEditPanel: WireframeEditPanel): JPanel {
        val panel = JPanel().apply {
            layout = GridLayout(5, 2)
        }
        val sliders = listOf(
            JSlider(JSlider.HORIZONTAL, 1, 10, context.n),
            JSlider(JSlider.HORIZONTAL, 2, 10, context.m),
            JSlider(JSlider.HORIZONTAL, 1, 20, context.m1),
            JSlider(JSlider.HORIZONTAL, 1, 50, context.gridStep)
        )
        val textFields = listOf(
            JTextField(context.n.toString()),
            JTextField(context.m.toString()),
            JTextField(context.m1.toString()),
            JTextField(context.gridStep.toString())
        )

        sliders.zip(textFields).forEachIndexed { index, (slider, textField) ->
            panel.add(
                JLabel(
                    when (index) {
                        0 -> "n:"
                        1 -> "m:"
                        2 -> "m1:"
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
            0 -> context.n = value
            1 -> context.m = value
            2 -> context.m1 = value
            3 -> context.gridStep = value
        }
    }

    private fun updateContextFromTextField(value: Int?, index: Int, sliders: List<JSlider>) {
        value?.let {
            when (index) {
                0 -> context.n = it
                1 -> context.m = it
                2 -> context.m1 = it
                3 -> context.gridStep = it
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
