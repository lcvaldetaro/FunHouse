package java.util.concurrent

class LinkedBlockingQueue<T> {
    private val list = mutableListOf<T>()

    fun put(element: T) {
        list.add(element)
    }

    fun take(): T {
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException("Thread interrupted")
        }
        if (list.isEmpty()) {
            val input = kotlinx.browser.window.prompt("Enter command:") ?: ""
            list.add(input as T)
        }
        if (Thread.currentThread().isInterrupted) {
            throw InterruptedException("Thread interrupted")
        }
        return list.removeAt(0)
    }

    fun poll(): T? {
        if (list.isEmpty()) return null
        return list.removeAt(0)
    }

    fun clear() {
        list.clear()
    }
}
