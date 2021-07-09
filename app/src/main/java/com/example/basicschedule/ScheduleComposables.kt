package com.example.basicschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class SchedulePresenter(
    coroutineScope: CoroutineScope
) {
    private val initialState = ScheduleViewModel(
        sampleEvents,
        sampleEvents.first().start.hour.coerceAtMost(8),
        sampleEvents.last().end.hour.coerceAtLeast(19),
        CurrentTimestamp(LocalTime.now(), true)
    )

    val scheduleState = MutableStateFlow(initialState)

    init {
        coroutineScope.launch(Dispatchers.Default) {
            delay(60000 - (LocalTime.now().second * 1000).toLong())
            while (true) {
                val newViewModel = scheduleState.value.copy(
                    timestamp = scheduleState.value.timestamp.copy(time = LocalTime.now()),
                    loading = false
                )
                scheduleState.emit(newViewModel)
                delay(60000)
            }
        }
    }
}

data class CurrentTimestamp(
    val time: LocalTime,
    val active: Boolean = false
)

data class ScheduleViewModel(
    val events: List<Event>,
    val firstHour: Int,
    val lastHour: Int,
    val timestamp: CurrentTimestamp,
    val loading: Boolean = false
)

data class Event(
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
)

val EventTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun HourLine() {
    Box(
        Modifier
            .height(1.dp)
            .absoluteOffset()
            .fillMaxSize()
            .background(Color.Red)
    )
}

@Composable
fun BasicEvent(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(event.color, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            text = "${event.start.format(EventTimeFormatter)} - ${event.end.format(EventTimeFormatter)}",
            style = MaterialTheme.typography.caption,
        )

        Text(
            text = event.name,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
        )

        if (event.description != null) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


private val sampleEvents = listOf(
    Event(
        name = "Coding",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-07-02T09:30:00"),
        end = LocalDateTime.parse("2021-07-02T11:30:00"),
        description = "Busy coding stuff",
    ),
    Event(
        name = "Daily",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-07-02T11:30:00"),
        end = LocalDateTime.parse("2021-07-02T11:45:00"),
        description = "Android Daily",
    ),
    Event(
        name = "Lunch break",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-07-02T12:00:00"),
        end = LocalDateTime.parse("2021-07-02T13:30:00"),
        description = "Mm Food",
    ),
    Event(
        name = "Coffee Time",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-07-02T14:00:00"),
        end = LocalDateTime.parse("2021-07-02T14:30:00"),
        description = "Talk about random stuff and drink coffee",
    ),
    Event(
        name = "Coding",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-07-02T15:00:00"),
        end = LocalDateTime.parse("2021-07-02T17:30:00"),
        description = "Busy coding stuff",
    )
)


private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private class HourLineDataModifier(
    val currentTimestamp: CurrentTimestamp
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = currentTimestamp
}

private fun Modifier.timestampData(currentTimestamp: CurrentTimestamp) = this.then(HourLineDataModifier(currentTimestamp))

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

private val HourFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Preview
@Composable
fun BasicSidebarLabel(
    modifier: Modifier = Modifier,
    time: LocalTime = LocalTime.now()
) {
    Box(
        modifier = modifier.height(30.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = time.format(HourFormatter),
            modifier = modifier
                .fillMaxHeight()
                .padding(4.dp)
        )
    }
}

@Composable
fun ScheduleSidebar(
    scheduleViewModel: ScheduleViewModel,
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        val firstHour = scheduleViewModel.firstHour
        val lastHour = scheduleViewModel.lastHour
        val startTime = LocalTime.MIN.plusHours(firstHour.toLong())
        repeat(lastHour - firstHour) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Composable
fun Schedule(
    schedulePresenter: SchedulePresenter,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { event -> BasicEvent(event = event) },
) {
    val hourHeight = 96.dp
    val verticalScrollState = rememberScrollState()
    val viewModel : ScheduleViewModel by schedulePresenter.scheduleState.collectAsState()
    Column(modifier = modifier) {
        if(viewModel.loading) {
            Row(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(modifier = Modifier.weight(1f)) {
                ScheduleSidebar(
                    scheduleViewModel = viewModel,
                    hourHeight = hourHeight,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                )
                BasicSchedule(
                    scheduleViewModel = viewModel,
                    eventContent = eventContent,
                    hourHeight = hourHeight,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(verticalScrollState)
                        .padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}

@Composable
fun BasicSchedule(
    scheduleViewModel: ScheduleViewModel,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { event -> BasicEvent(event = event) },
    hourHeight: Dp,
) {
    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
    val firstHour = scheduleViewModel.firstHour
    val lastHour = scheduleViewModel.lastHour
    val startOffset =
        ChronoUnit.MINUTES.between(LocalTime.MIN, scheduleViewModel.events.first().start) -
                ChronoUnit.MINUTES.between(
                    LocalTime.MIN.plusHours(firstHour.toLong()),
                    scheduleViewModel.events.first().start
                )
    Layout(
        content = {
            scheduleViewModel.events.sortedBy(Event::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
            }
            Box(modifier = Modifier.timestampData(scheduleViewModel.timestamp)) {
                HourLine()
            }
        },
        modifier = modifier
            .drawBehind {
                repeat(lastHour - firstHour) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it) * hourHeight.toPx() + 17.dp.toPx()),
                        end = Offset(size.width, (it) * hourHeight.toPx() + 17.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
    ) { measureables, constraints ->
        val height = hourHeight.roundToPx() * 24
        val width = constraints.maxWidth
        val placeablesWithEvents = measureables.filter { it.parentData is Event }.map { measurable ->
            val event = measurable.parentData as Event
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, event)
        }

        val timestampMeasurable = measureables.first { it.parentData is CurrentTimestamp }
        val timestampData = timestampMeasurable.parentData as CurrentTimestamp
        val timestampPlaceable = if (timestampData.active) {
            timestampMeasurable.measure(
                constraints.copy(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = 1.dp.roundToPx(),
                    maxHeight = 1.dp.roundToPx()
                )
            )
        } else null


        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, data) ->
                val eventOffsetMinutes = ChronoUnit.MINUTES.between(
                    LocalTime.MIN,
                    data.start.toLocalTime()
                ) - startOffset
                val eventY =
                    ((eventOffsetMinutes / 60f) * hourHeight.toPx() + 17.dp.toPx()).roundToInt()
                val eventX = 0
                placeable.place(eventX, eventY)
            }


            timestampPlaceable?.let {
                val lineX = 0
                val offset = ChronoUnit.MINUTES.between(LocalTime.MIN, timestampData.time) - startOffset
                val lineY = ((offset / 60f) * hourHeight.toPx() + 17.dp.toPx()).roundToInt()
                it.place(lineX, lineY)
            }
        }
    }
}