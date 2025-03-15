package ua.railian.mvi.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.withContext
import ua.railian.mvi.holder.EventsHolder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
@Suppress("ComposableNaming")
public inline fun <reified EVENT> EventsHolder<EVENT>.collectEvents(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onEach: suspend (event: EVENT) -> Unit,
) {
    LaunchedEffect(this, lifecycleOwner, minActiveState, context) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                events.collect { onEach(it) }
            } else withContext(context) {
                events.collect { onEach(it) }
            }
        }
    }
}
