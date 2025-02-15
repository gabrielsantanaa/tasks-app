package org.gabrielsantana.tasks.data.source.local.model

import kotlinx.datetime.Instant
import org.gabrielsantana.tasks.TaskEntity
import org.gabrielsantana.tasks.data.convertToBoolean
import org.gabrielsantana.tasks.data.model.Task

//The model is auto generated by the SQLDelight, this file is just for map function

fun TaskEntity.asTask(): Task = Task(
    id = id.toInt(),
    title = title,
    description = description,
    isCompleted = isCompleted.convertToBoolean(),
    createdAt = Instant.fromEpochMilliseconds(createdAtTimestamp),
    completedAt = completedAtTimestamp?.let { Instant.fromEpochMilliseconds(it) },
    updatedAt = updatedAtTimestamp?.let { Instant.fromEpochMilliseconds(it) }
)