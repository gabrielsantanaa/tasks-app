package org.gabrielsantana.tasks.data

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.gabrielsantana.tasks.data.model.Task
import org.gabrielsantana.tasks.data.scheduler.TaskSyncScheduler
import org.gabrielsantana.tasks.data.source.local.TasksLocalDataSource
import org.gabrielsantana.tasks.data.source.local.model.asTask
import org.gabrielsantana.tasks.data.source.remote.TasksRemoteDataSource

class TasksRepository(
    private val taskSyncScheduler: TaskSyncScheduler,
    private val connectivity: Connectivity,
    private val remoteDataSource: TasksRemoteDataSource,
    private val localDataSource: TasksLocalDataSource
) {
    fun getTasks(): Flow<List<Task>> = localDataSource.getAll().map { it.map { entity -> entity.asTask() } }

    fun deleteTask(uuid: String) = localDataSource.delete(uuid)

    fun updateTask(uuid: String, isChecked: Boolean) {
        localDataSource.updateIsChecked(uuid, isChecked)
    }

    suspend fun createTask(title: String, description: String, isCompleted: Boolean = false) {
        val newTask = localDataSource.insert(title, description, isCompleted)
        taskSyncScheduler.scheduleTask(taskUuid = newTask.uuid)
    }
}