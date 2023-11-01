package gemini.foundation

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import gemini.asset.Asset
import gemini.asset.Image
import gemini.asset.Source
import gemini.geometry.*

abstract class Atlas<K : Any> : Asset() {
    abstract fun DrawScope.draw(key: K)
}

private open class ImageMap<K : Any>(private val image: Image, private val find: (key: K, rectangle: IntRectangle) -> Unit) : Atlas<K>() {

    override val isLoaded: Boolean get() = image.isLoaded
    private val rectangle = IntRectangle() // not normally thread safe, but drawing only happens on the main thread

    override fun DrawScope.draw(key: K) {
        find(key, rectangle)
        if (rectangle.isNotEmpty) {
            image.run { drawPart(rectangle.topLeft, rectangle.size) }
        }
    }

    override suspend fun load(source: Source) = image.load(source)
    override fun release() = image.release()
}

fun <K : Any> imageMap(image: Image, find: (key: K, rectangle: IntRectangle) -> Unit): Atlas<K> = ImageMap(image, find)

fun tileMap(image: Image, tileSize: IntSize, inset: IntOffset = IntOffset.Zero): Atlas<IntOffset> = imageMap(image) { key, rectangle ->
    val offset = key * tileSize + inset
    val size = image.size
    if (key.x < 0 || key.y < 0 || offset.x + tileSize.width > size.width || offset.y + tileSize.height > size.height) {
        rectangle.setFrom(IntRectangle.Zero)
    } else {
        rectangle.set(offset, tileSize)
    }
}
