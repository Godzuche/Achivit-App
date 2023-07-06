package com.godzuche.achivitapp.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val achivitDispatcher: AchivitDispatchers)

enum class AchivitDispatchers {
    DEFAULT,
    IO
}