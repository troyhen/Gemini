package gemini

import androidx.compose.ui.text.TextMeasurer

class SceneScope(val textMeasurer: TextMeasurer) : Scene() {

    var camera = Camera()

    fun build(): Stage {
        val stage = Stage(textMeasurer)
        stage.set(camera)
        stage.replaceAll(this)
        return stage
    }
}
