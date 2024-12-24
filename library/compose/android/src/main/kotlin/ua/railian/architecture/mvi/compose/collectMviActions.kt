package ua.railian.architecture.mvi.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.withContext
import ua.railian.architecture.mvi.ActionsHolder
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
@Suppress("ComposableNaming")
public inline fun <reified ACTION> ActionsHolder<ACTION>.collectMviActions(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onEach: suspend (action: ACTION) -> Unit
) {
    LaunchedEffect(this, lifecycleOwner, minActiveState, context) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                actions.collect { onEach(it) }
            } else withContext(context) {
                actions.collect { onEach(it) }
            }
        }
    }
}
