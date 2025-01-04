package ua.railian.mvi.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ua.railian.mvi.MviModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
public inline fun <reified STATE> MviModel<STATE, *>.renderState(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): STATE {
    return state.collectAsStateWithLifecycle(
        lifecycleOwner = lifecycleOwner,
        minActiveState = minActiveState,
        context = context,
    ).value
}
