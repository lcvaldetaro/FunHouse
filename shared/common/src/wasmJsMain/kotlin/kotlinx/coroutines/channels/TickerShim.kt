package kotlinx.coroutines.channels

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@ObsoleteCoroutinesApi
fun ticker(
    delayMillis: Long,
    initialDelayMillis: Long = 0,
    context: kotlin.coroutines.CoroutineContext = kotlin.coroutines.EmptyCoroutineContext,
    mode: Any? = null
): ReceiveChannel<Unit> {
    val channel = Channel<Unit>(Channel.CONFLATED)
    @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
    GlobalScope.launch(context) {
        delay(initialDelayMillis)
        while (true) {
            channel.send(Unit)
            delay(delayMillis)
        }
    }
    return channel
}
