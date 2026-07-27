package java.io

interface Serializable

interface File {
    val path: String
    val absolutePath: String
    val isAbsolute: Boolean
    val name: String
    fun exists(): Boolean
    fun mkdir(): Boolean
    fun mkdirs(): Boolean
    fun delete(): Boolean
    fun readText(): String
    fun writeText(text: String)
    fun readBytes(): ByteArray

    fun bufferedReader(): BufferedReader
    fun bufferedWriter(): BufferedWriter
    fun copyTo(target: File, overwrite: Boolean = false): File
    fun forEachLine(action: (String) -> Unit)
    fun printWriter(): PrintWriter
}

class FileImpl(override val path: String) : File {
    override val absolutePath: String get() = path
    override val isAbsolute: Boolean get() = false
    override val name: String get() = path.substringAfterLast('/')
    
    override fun exists(): Boolean {
        val fileName = path.substringAfterLast('/')
        return com.funhouse.shared.common.utils.AssetCache.cache.containsKey(fileName) ||
               (kotlinx.browser.window.localStorage.getItem(fileName) != null)
    }

    override fun mkdir(): Boolean = true
    override fun mkdirs(): Boolean = true
    
    override fun delete(): Boolean {
        val fileName = path.substringAfterLast('/')
        kotlinx.browser.window.localStorage.removeItem(fileName)
        return true
    }

    override fun readText(): String {
        val fileName = path.substringAfterLast('/')
        return com.funhouse.shared.common.utils.AssetCache.cache[fileName] 
            ?: kotlinx.browser.window.localStorage.getItem(fileName) 
            ?: ""
    }

    override fun writeText(text: String) {
        val fileName = path.substringAfterLast('/')
        kotlinx.browser.window.localStorage.setItem(fileName, text)
    }

    override fun readBytes(): ByteArray = readText().encodeToByteArray()

    override fun bufferedReader(): BufferedReader = BufferedReader(this)
    override fun bufferedWriter(): BufferedWriter = BufferedWriter(this)
    override fun copyTo(target: File, overwrite: Boolean): File {
        if (overwrite || !target.exists()) {
            target.writeText(this.readText())
        }
        return target
    }
    override fun forEachLine(action: (String) -> Unit) {
        val text = readText()
        if (text.isNotEmpty()) {
            text.lines().forEach(action)
        }
    }
    override fun printWriter(): PrintWriter = PrintWriter(this)
}

fun File(path: String): File = FileImpl(path)
fun File(parent: String?, child: String): File = FileImpl(if (parent != null) "$parent/$child" else child)
fun File(parent: File?, child: String): File = FileImpl(if (parent != null) "${parent.path}/$child" else child)

class FileReader(val file: File)
class FileWriter(val file: File)

class IOException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class BufferedReader(val source: Any) : AutoCloseable {
    private val content: String = when (source) {
        is FileReader -> source.file.readText()
        is File -> source.readText()
        else -> ""
    }
    private val lines = content.lines()
    private var index = 0
    private var charIndex = 0

    fun readLine(): String? {
        if (index < lines.size) {
            return lines[index++]
        }
        return null
    }

    fun read(): Int {
        if (charIndex < content.length) {
            return content[charIndex++].code
        }
        return -1
    }

    override fun close() {}
}

class BufferedWriter(val source: Any?) : AutoCloseable {
    private val file: File? = when (source) {
        is FileWriter -> source.file
        is File -> source
        else -> null
    }
    private val sb = StringBuilder()
    
    fun write(str: String) {
        sb.append(str)
    }
    fun newLine() {
        sb.append("\n")
    }
    override fun close() {
        file?.writeText(sb.toString())
    }
    fun flush() {
        file?.writeText(sb.toString())
    }
}

class PrintWriter(val source: Any?) : AutoCloseable {
    private val file: File? = when (source) {
        is File -> source
        else -> null
    }
    private val sb = StringBuilder()
    
    fun println(x: Any?) {
        sb.append(x.toString()).append("\n")
    }
    fun println(x: String?) {
        sb.append(x ?: "null").append("\n")
    }
    fun println(x: Int) {
        sb.append(x).append("\n")
    }
    fun println(x: Long) {
        sb.append(x).append("\n")
    }
    fun println(x: Double) {
        sb.append(x).append("\n")
    }
    fun println(x: Float) {
        sb.append(x).append("\n")
    }
    fun println(x: Char) {
        sb.append(x).append("\n")
    }
    fun println(x: Boolean) {
        sb.append(x).append("\n")
    }
    override fun close() {
        file?.writeText(sb.toString())
    }
}

class StreamTokenizer(val reader: BufferedReader) {
    private var tokens: List<String> = emptyList()
    private var tokenIndex = 0
    
    var sval: String = "0"
    var tval: Int = 0
    var nval: Double = 0.0
    
    fun resetSyntax() {}
    fun whitespaceChars(low: Int, hi: Int) {}
    fun wordChars(low: Int, hi: Int) {}
    
    fun nextToken(): Int {
        if (tokens.isEmpty()) {
            val builder = StringBuilder()
            var c = reader.read()
            while (c != -1) {
                builder.append(c.toChar())
                c = reader.read()
            }
            tokens = builder.toString().split(Regex("\\s+")).filter { it.isNotEmpty() }
        }
        if (tokenIndex < tokens.size) {
            sval = tokens[tokenIndex++]
            return 0
        }
        sval = "0"
        return -1
    }
}
