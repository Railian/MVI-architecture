package ua.railian.architecture.mvi.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
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
import org.koin.viewmodel.factory.AndroidParametersHolder
import org.koin.viewmodel.resolveViewModel
import ua.railian.architecture.mvi.MviModel
import kotlin.reflect.KClass

public inline fun <reified T> Module.mviViewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
): KoinDefinition<T> where T : MviModel<*, *>, T : ViewModel {
    return viewModel(qualifier, definition)
}

public inline fun <reified T : MviModel<*, *>> Module.mviModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
): KoinDefinition<T> {
    return factory(qualifier, definition)
}

@OptIn(KoinInternalApi::class)
@Composable
public inline fun <reified T : MviModel<*, *>> koinMviModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"),
    key: String? = null,
    extras: CreationExtras = defaultExtras(viewModelStoreOwner),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T {
    if (LocalInspectionMode.current) {
        return remember(qualifier, extras, parameters) {
            scope.getWithParameters(
                clazz = T::class,
                qualifier = qualifier,
                parameters = AndroidParametersHolder(
                    initialValues = parameters,
                    extras = extras,
                ),
            )
        }
    } else {
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
}
