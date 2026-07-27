package kotlin.concurrent

fun thread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    contextClassLoader: Any? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
): Thread {
    val t = Thread()
    Thread.register(t)
    kotlinx.browser.window.setTimeout({
        val old = Thread.currentThread()
        Thread.setCurrentThread(t)
        try {
            block()
        } catch (e: Throwable) {
            // Safe catch
        } finally {
            Thread.setCurrentThread(old)
        }
        null
    }, 0)
    return t
}
