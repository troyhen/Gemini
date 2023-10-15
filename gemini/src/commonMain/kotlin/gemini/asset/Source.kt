package gemini.asset

import java.io.File
import java.net.URL

sealed class Source

data class SourceAsset(val name: String) : Source()
data class SourceData(val data: Any) : Source()
data class SourceFile(val file: File) : Source()
data class SourcePath(val path: String) : Source()
data class SourceResource(val id: Int) : Source()
data class SourceUrl(val url: URL) : Source()
