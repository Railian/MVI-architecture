package ua.railian.mvi.koin.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.compose.currentKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.defaultExtras
import org.koin.viewmodel.resolveViewModel
import ua.railian.mvi.MviModel
import kotlin.reflect.KClass

public inline fun <reified T> Module.mviViewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
): KoinDefinition<T> where T : MviModel<*, *>, T : ViewModel {
    return viewModel(qualifier, definition)
}

@OptIn(KoinInternalApi::class)
@Composable
public inline fun <reified T : MviModel<*, *>> koinMviViewModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"),
    key: String? = null,
    extras: CreationExtras = defaultExtras(viewModelStoreOwner),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T {
    @Suppress("UNCHECKED_CAST")
    return resolveViewModel(
        vmClass = T::class as KClass<ViewModel>,
        viewModelStore = viewModelStoreOwner.viewModelStore,
        key = key,
        extras = extras,
        qualifier = qualifier,
        scope = scope,
        parameters = parameters,
    ) as T
}
