package org.gabrielsantana.tasks.features.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    taskCreated: Boolean,
    onTaskCreated: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(taskCreated) {
        if (taskCreated) {
            hostState.showSnackbar("Task created")
            onTaskCreated()
        }
    }


    HomeContent(
        uiState = uiState,
        hostState = hostState,
        onCreateTaskClick = onNavigateToCreateTask,
        onTaskCheckedChange = viewModel::updateTask,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    onCreateTaskClick: () -> Unit,
    onTaskCheckedChange: (newValue: Boolean, model: TaskUiModel) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Create task")
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp , end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(it)
        ) {
            items(uiState.tasks, key = { task -> task.id }) { task ->
                TaskItem(
                    title = task.title,
                    description = task.description,
                    isChecked = task.isChecked,
                    onCheckedChange = { onTaskCheckedChange(it, task) },
                    Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    HomeContent(
        uiState = HomeUiState(
            tasks = listOf(
                TaskUiModel(1, "Task 1", "Description 1", false),
                TaskUiModel(2, "Task 2", "Description 2", true),
                TaskUiModel(3, "Task 3", "Description 3", false),
            )
        ),
        onCreateTaskClick = {},
        onTaskCheckedChange = { _, _ -> },
    )
}