package gemini.asset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import gemini.foundation.Draw
import gemini.geometry.max
import io.kamel.core.*
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.ResourceConfig
import io.kamel.core.config.ResourceConfigBuilder
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.getDataSourceEnding
import kotlin.math.max

class Image : Asset(), Draw {
    private var resource: Resource<*>? = null
    override val isLoaded: Boolean = resource != null && resource?.isSuccess == true

    val size: IntSize get() {
        return when (val value = resource?.getOrNull()) {
            is ImageBitmap -> IntSize(value.width, value.height)
            else -> IntSize.Zero
        }
    }

    override fun DrawScope.draw() {
        when (val value = resource?.getOrNull()) {
            is ImageBitmap -> scale(1f / max(value.width, value.height).coerceAtLeast(1), Offset.Zero) {
                drawImage(value)
            }
        }
    }

    fun DrawScope.drawPart(fromOffset: IntOffset, fromSize: IntSize, toOffset: IntOffset = IntOffset.Zero, toSize: IntSize = fromSize) {
        scale(1f / toSize.max, Offset.Zero) {
            when (val value = resource?.getOrNull()) {
                is ImageBitmap -> drawImage(value, fromOffset, fromSize, toOffset, toSize)
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
            is SourceRes -> source.id
            is SourceURI -> source.uri
            is SourceUrl -> source.url
            is SourceURL -> source.url
        }
        println("data $data")
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
                println("resource $resource")
            }
        }
    }

    override fun release() {
        resource = null
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
