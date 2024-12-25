package ua.railian.architecture.mvi.simple

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

internal val completedJob = Job().apply(CompletableJob::complete)
