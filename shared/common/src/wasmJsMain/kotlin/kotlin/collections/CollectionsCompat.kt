package kotlin.collections

inline fun <T> MutableCollection<T>.removeIf(filter: (T) -> Boolean): Boolean {
    val iterator = iterator()
    var removed = false
    while (iterator.hasNext()) {
        if (filter(iterator.next())) {
            iterator.remove()
            removed = true
        }
    }
    return removed
}
