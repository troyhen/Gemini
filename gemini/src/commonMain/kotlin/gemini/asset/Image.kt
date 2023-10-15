package gemini.asset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import gemini.geometry.Rectangle
import gemini.geometry.max
import gemini.geometry.size
import io.kamel.core.*
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.ResourceConfig
import io.kamel.core.config.ResourceConfigBuilder
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.getDataSourceEnding
import kotlin.math.max

class Image : Asset() {
    private var resource: Resource<*>? = null
    override val isLoaded: Boolean = resource != null && resource?.isSuccess == true

    fun DrawScope.draw(rectangle: Rectangle) {
        when (val value = resource?.getOrNull()) {
            is ImageBitmap -> scale(rectangle.size.max / max(value.width.toFloat(), value.height.toFloat()), Offset.Zero) {
                drawImage(value)
            }
        }
    }

    override suspend fun load(source: Source) {
        val kamelConfig = kamelConfig ?: return
        val resourceConfig = resourceConfig ?: return
        val data = when (source) {
            is SourceAsset -> "assets://${source.name}"
            is SourceData -> source.data
            is SourceFile -> source.file
            is SourcePath -> source.path
            is SourceResource -> source.id
            is SourceUrl -> source.url
        }
        val ending = getDataSourceEnding(data)

        resource = when (ending) {
            "svg" -> error("SVG image not supported") // kamelConfig.loadCachedResourceOrNull(data, kamelConfig.svgCache)
            "xml" -> error("XML image not supported")// kamelConfig.loadCachedResourceOrNull(data, kamelConfig.imageVectorCache)
            else -> kamelConfig.loadCachedResourceOrNull(data, kamelConfig.imageBitmapCache)
        }

        if (resource == null) {
            when (ending) {
                "svg" -> kamelConfig.loadSvgResource(data, resourceConfig)
                "xml" -> kamelConfig.loadImageVectorResource(data, resourceConfig)
                else -> kamelConfig.loadImageBitmapResource(data, resourceConfig)
            }.collect {
                resource = it
            }
        }
    }

    companion object {
        var kamelConfig: KamelConfig? = null
        var resourceConfig: ResourceConfig? = null
    }
}

@Composable
fun SetupImages() {
    val screenDensity = LocalDensity.current
    val scope = rememberCoroutineScope()
    Image.kamelConfig = LocalKamelConfig.current
    Image.resourceConfig = remember(screenDensity, scope) {
        ResourceConfigBuilder(scope.coroutineContext)
            .apply { density = screenDensity }
            .build()
    }
}
