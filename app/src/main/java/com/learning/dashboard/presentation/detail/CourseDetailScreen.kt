package com.learning.dashboard.presentation.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.dashboard.domain.model.Lesson
import com.learning.dashboard.presentation.components.ErrorStateView
import com.learning.dashboard.presentation.components.LoadingStateView
import com.learning.dashboard.presentation.components.ProgressBarWithLabel
import com.learning.dashboard.presentation.theme.Amber100
import com.learning.dashboard.presentation.theme.Amber800
import com.learning.dashboard.presentation.theme.Emerald100
import com.learning.dashboard.presentation.theme.Emerald500
import com.learning.dashboard.presentation.theme.Emerald800
import com.learning.dashboard.presentation.theme.Indigo50
import com.learning.dashboard.presentation.theme.Indigo600
import com.learning.dashboard.presentation.theme.Slate200
import com.learning.dashboard.presentation.theme.Slate400
import com.learning.dashboard.presentation.theme.Slate500
import com.learning.dashboard.presentation.theme.Slate600
import com.learning.dashboard.presentation.theme.Slate800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    viewModel: CourseDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Course Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Indigo600
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        when (val currentState = state) {
            is CourseDetailUiState.Loading -> {
                LoadingStateView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    message = "Loading course curriculum..."
                )
            }
            is CourseDetailUiState.Success -> {
                val course = currentState.course
                val completedCount = course.lessons.count { it.isCompleted }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Header Overview Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Slate400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Instructor: ${course.instructor}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Slate600
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Progress Indicator with live percentage
                                ProgressBarWithLabel(progress = course.progress)

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "$completedCount of ${course.lessons.size} lessons completed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    // Section Title
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lessons Curriculum",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Tap to toggle",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400
                            )
                        }
                    }

                    // Lesson Items
                    items(course.lessons, key = { it.id }) { lesson ->
                        LessonItemCard(
                            lesson = lesson,
                            onToggle = { viewModel.toggleLessonStatus(lesson.id) }
                        )
                    }
                }
            }
            is CourseDetailUiState.Error -> {
                ErrorStateView(
                    message = currentState.message,
                    onRetry = { viewModel.loadCourseDetail() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun LessonItemCard(
    lesson: Lesson,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (lesson.isCompleted) Emerald500.copy(alpha = 0.4f) else Color.Transparent,
        label = "lessonBorder"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lesson.isCompleted) Emerald100.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (lesson.isCompleted) Emerald500.copy(alpha = 0.3f) else Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = lesson.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Emerald500,
                        uncheckedColor = Slate400
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (lesson.isCompleted) FontWeight.Medium else FontWeight.Normal,
                        color = if (lesson.isCompleted) Slate800 else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (lesson.isCompleted) TextDecoration.None else TextDecoration.None
                    )
                    Text(
                        text = "Lesson ${lesson.orderIndex}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                }
            }

            // Status Badge: ✓ Completed or ○ Pending
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (lesson.isCompleted) Emerald100 else Amber100,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (lesson.isCompleted) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (lesson.isCompleted) Emerald800 else Amber800,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (lesson.isCompleted) "Completed" else "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (lesson.isCompleted) Emerald800 else Amber800
                    )
                }
            }
        }
    }
}
