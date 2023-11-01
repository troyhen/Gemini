package gemini.asset

import io.ktor.http.Url
import java.io.File
import java.net.URI
import java.net.URL

sealed class Source

data class SourceAsset(val name: String) : Source()
data class SourceData(val data: Any) : Source()
data class SourceFile(val file: File) : Source()
data class SourcePath(val path: String) : Source()
data class SourceRes(val id: Int) : Source()
data class SourceURI(val uri: URI) : Source()
data class SourceUrl(val url: Url) : Source()
data class SourceURL(val url: URL) : Source()

val <T : Any> T.asSource: Source
    get() = when (this) {
        is File -> SourceFile(this)
        is Int -> SourceRes(this)
        is String -> when {
            startsWith("asset:") -> SourceAsset(substring(6))
            startsWith("assets://") -> SourceAsset(substring(9))
            startsWith("file:") || this.startsWith("http") -> SourceURL(URL(this))
            startsWith("resource:") -> SourceUrl(Url(this))
            startsWith("/") -> SourceFile(File(this))
            else -> SourceData(this)
        }

        is URI -> SourceURI(this)
        is Url -> SourceUrl(this)
        is URL -> SourceURL(this)
        else -> SourceData(this)
    }