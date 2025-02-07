package org.gabrielsantana.tasks.features.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.gabrielsantana.tasks.Task
import org.gabrielsantana.tasks.data.TasksRepository
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private var getTasksJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTasksJob = viewModelScope.launch { loadTasks() }
    }

    fun selectTask(taskIndex: Int) = _uiState.update { state ->
        if (state.selectedTasksIndex.contains(taskIndex)) {
            state.copy(selectedTasksIndex = state.selectedTasksIndex.toMutableSet().apply { remove(taskIndex) })
        } else {
            state.copy(selectedTasksIndex = state.selectedTasksIndex.toMutableSet().apply { add(taskIndex) })
        }
    }

    fun clearSelectedTasks() = _uiState.update { state ->
        state.copy(selectedTasksIndex = emptySet())
    }

    fun deleteSelectedTasks(): Unit = with(_uiState.value) {
        selectedTasksIndex.forEach { taskIndex ->
            tasksRepository.deleteTask(tasks[taskIndex].id.toLong())
        }
        clearSelectedTasks()
    }

    fun deleteTask(task: TaskUiModel) {
        tasksRepository.deleteTask(task.id.toLong())
    }

    fun searchTasks(query: String) {
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch {
            delay(300.milliseconds)
            if (query.isNotEmpty()) {
                tasksRepository.getTasks().collect { tasks ->
                    val filteredTasks = tasks.filter { task ->
                        task.title.contains(query, ignoreCase = true)
                    }.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(tasks = filteredTasks)
                    }
                }
            } else {
                loadTasks()
            }
        }

    }

    fun selectTaskFilter(newFilter: TaskFilter) {
        getTasksJob?.cancel()
        getTasksJob = viewModelScope.launch {
            val tasks = tasksRepository.getTasks().collect { tasks ->
                _uiState.update { state ->
                    state.copy(
                        selectedTaskFilter = newFilter,
                        tasks = tasks.filter { newFilter.predicate(it) }.map { it.toUiModel() })
                }
            }

        }
    }

    private suspend fun loadTasks() {
        tasksRepository.getTasks().collect { value ->
            _uiState.update { state ->
                val newTasks = value
                    .filter {
                        _uiState.value.selectedTaskFilter.predicate(it)
                    }.map {
                        it.toUiModel()
                    }
                state.copy(tasks = newTasks)
            }
        }
    }

    fun updateTask(isChecked: Boolean, task: TaskUiModel) {
        viewModelScope.launch {
            tasksRepository.updateTask(task.id.toLong(), isChecked)
        }
    }

}


fun Task.toUiModel(): TaskUiModel {
    return TaskUiModel(id.toInt(), title, description, isChecked > 0)
}
