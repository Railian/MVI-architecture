package ua.railian.mvi.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import org.koin.compose.LocalKoinApplication
import org.koin.compose.LocalKoinScope
import org.koin.compose.application.rememberKoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

@OptIn(KoinInternalApi::class)
@Composable
public fun KoinPreview(
    vararg modules: Module,
    application: KoinAppDeclaration? = null,
    content: @Composable () -> Unit,
) {
    when (KoinPlatformTools.defaultContext().getOrNull()) {
        null -> {
            val koin = rememberKoinApplication(koinApplication(application))
            koin.loadModules(remember(modules, modules::toList))
            CompositionLocalProvider(
                LocalKoinApplication provides koin,
                LocalKoinScope provides koin.scopeRegistry.rootScope,
                content = content,
            )
        }

        else -> content()
    }
}

@Composable
public fun KoinPreview(
    module: ModuleDeclaration? = null,
    application: KoinAppDeclaration? = null,
    content: @Composable () -> Unit,
) {
    KoinPreview(
        modules = when (module) {
            null -> emptyArray()
            else -> arrayOf(module(moduleDeclaration = module))
        },
        application = application,
        content = content,
    )
}
